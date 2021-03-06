package org.indiv.dls.onerepmax.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.indiv.dls.onerepmax.data.db.Exercise
import org.indiv.dls.onerepmax.data.db.ExerciseDao
import org.indiv.dls.onerepmax.data.db.ExerciseDatabase
import org.indiv.dls.onerepmax.data.db.ExerciseDay
import org.indiv.dls.onerepmax.data.db.ExerciseDayDao
import org.indiv.dls.onerepmax.data.db.ExerciseDayEntry
import org.indiv.dls.onerepmax.data.db.ExerciseDayEntryDao
import org.indiv.dls.onerepmax.data.db.ExerciseWithDays
import org.indiv.dls.onerepmax.data.db.IdGenerator
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class ExerciseRepository @Inject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val statsFileReader: StatsFileReader,
    private val statsCalculator: StatsCalculator,
    private val exerciseDatabase: ExerciseDatabase,
    private val exerciseDao: ExerciseDao,
    private val exerciseDayDao: ExerciseDayDao,
    private val exerciseDayEntryDao: ExerciseDayEntryDao,
    private val idGenerator: IdGenerator
) {
    // Boolean represent whether top-level exercise updated
    private val _dataChangeLiveData = MutableLiveData<Boolean>()
    val dataChangeLiveData: LiveData<Boolean> = _dataChangeLiveData

    fun isDarkModeInSettings(): Boolean {
        return sharedPrefsHelper.isDarkMode()
    }

    fun persistDarkMode(dark: Boolean) {
        sharedPrefsHelper.persistDarkMode(dark)
    }

    suspend fun getExerciseSummaries(): List<ExerciseSummary> {
        return fetchExerciseSummaries().takeIf { it.isNotEmpty() } ?: run {
            persistAll(loadFromFile())
            fetchExerciseSummaries()
        }
    }

    suspend fun resetToFile() {
        withContext(Dispatchers.IO) {
            exerciseDatabase.clearAllTables()
        }
        persistAll(loadFromFile())
        _dataChangeLiveData.postValue(true)
    }

    suspend fun getSingleExerciseDetail(exerciseId: String): ExerciseWithStats? {
        return exerciseDao.getExerciseWithDays(exerciseId)?.let {
            ExerciseWithStats(
                exerciseSummary = ExerciseSummary(
                    exerciseId = exerciseId,
                    exerciseName = it.exercise.name,
                    oneRepMaxPersonalRecord = it.exercise.bestOneRepMax.toUInt()
                ),
                singleDayResults = convertToSingleDayResults(it)
            )
        }
    }

    suspend fun addStatsRecord(statsRecord: StatsRecord) {
        val exercise = exerciseDao.getExercise(statsRecord.exerciseName)
        val candidate = statsCalculator.calculateSingleExercise(statsRecord.exerciseName,
            listOf(statsRecord))
        if (exercise != null) {
            val exerciseDay = exerciseDayDao.getExerciseDay(exercise.id, statsRecord.dateOfWorkout)
            val candidateExerciseDay = candidate.daysWithFullDetail.first()
            if (exerciseDay != null) {
                // Persist only the new entry
                candidateExerciseDay.calculatedStatsRecords.forEach {
                    persistExerciseDayEntry(exerciseDay.id, it.statsRecord, it.oneRepMax)
                }

                // Update the day's average value
                val oneRepMaxAverageForDay = exerciseDayEntryDao.getAverageOneRepMax(exerciseDay.id)
                    ?.roundToInt() ?: 0
                exerciseDayDao.update(exerciseDay.copy(oneRepMax = oneRepMaxAverageForDay))

                // Update the exercise aggregate value if needed
                val topLevelUpdated = if (oneRepMaxAverageForDay > exerciseDay.oneRepMax) {
                    updateExerciseIfExceeded(oneRepMaxAverageForDay, exercise)
                } else if (oneRepMaxAverageForDay < exerciseDay.oneRepMax
                    && exercise.bestOneRepMax == exerciseDay.oneRepMax) {
                    // The personal record was depending on the current day whose value just went down
                    exerciseDao.update(exercise.copy(
                        bestOneRepMax = exerciseDayDao.getBestOneRepMax(exercise.id) ?: 0)
                    )
                    true
                } else false
                _dataChangeLiveData.postValue(topLevelUpdated)
            } else {
                // Persist the new day and entry, and update the exercise personal best if exceeded
                persistExerciseDay(exercise.id, candidateExerciseDay)
                val topLevelUpdated = updateExerciseIfExceeded(
                    candidate.oneRepMaxPersonalRecord.toInt(), exercise)
                _dataChangeLiveData.postValue(topLevelUpdated)
            }
        } else {
            // All new exercise, so persist it all
            persistExercise(candidate)
            _dataChangeLiveData.postValue(true)
        }
    }

    private suspend fun updateExerciseIfExceeded(candidateBest: Int, exercise: Exercise): Boolean {
        if (candidateBest > exercise.bestOneRepMax) {
            exerciseDao.update(exercise.copy(bestOneRepMax = candidateBest))
            return true
        }
        return false
    }

    private fun convertToSingleDayResults(it: ExerciseWithDays): List<SingleDayResult> {
        return it.days.map { SingleDayResult(it.date, it.oneRepMax.toUInt()) }
    }

    private suspend fun loadFromFile(): List<ExerciseWithFullDetail> {
        val statsRecords = statsFileReader.readFile()
        return statsCalculator.calculate(statsRecords)
    }

    private suspend fun fetchExerciseSummaries(): List<ExerciseSummary> {
        return exerciseDao.getAll().map { ExerciseSummary(it.id, it.name, it.bestOneRepMax.toUInt()) }
    }

    private suspend fun persistAll(exerciseData: List<ExerciseWithFullDetail>) {
        exerciseData.forEach { persistExercise(it) }
    }

    private suspend fun persistExercise(exerciseWithFullDetail: ExerciseWithFullDetail) {
        val exerciseId = idGenerator.exerciseId()
        exerciseDao.insert(Exercise(
            id = exerciseId,
            name = exerciseWithFullDetail.exerciseName,
            bestOneRepMax = exerciseWithFullDetail.oneRepMaxPersonalRecord.toInt()
        ))
        exerciseWithFullDetail.daysWithFullDetail.forEach {
            persistExerciseDay(exerciseId, it)
        }
    }

    private suspend fun persistExerciseDay(exerciseId: String, dayWithFullDetail: DayWithFullDetail) {
        val exerciseDayId = idGenerator.exerciseDayId()
        exerciseDayDao.insert(ExerciseDay(
            id = exerciseDayId,
            exerciseId = exerciseId,
            date = dayWithFullDetail.date,
            oneRepMax = dayWithFullDetail.oneRepMax.toInt()
        ))
        dayWithFullDetail.calculatedStatsRecords.forEach {
            persistExerciseDayEntry(exerciseDayId, it.statsRecord, it.oneRepMax)
        }
    }

    private suspend fun persistExerciseDayEntry(
        exerciseDayId: String,
        statsRecord: StatsRecord,
        oneRepMax: Double
    ) {
        exerciseDayEntryDao.insert(ExerciseDayEntry(
            id = idGenerator.exerciseDayEntryId(),
            exerciseDayId = exerciseDayId,
            sets = statsRecord.sets.toInt(),
            reps = statsRecord.reps.toInt(),
            weight = statsRecord.weight.toInt(),
            oneRepMax = oneRepMax
        ))
    }
}
