package org.indiv.dls.onerepmax.data

import org.indiv.dls.onerepmax.data.db.Exercise
import org.indiv.dls.onerepmax.data.db.ExerciseDao
import org.indiv.dls.onerepmax.data.db.ExerciseDay
import org.indiv.dls.onerepmax.data.db.ExerciseDayDao
import org.indiv.dls.onerepmax.data.db.ExerciseDayEntry
import org.indiv.dls.onerepmax.data.db.ExerciseDayEntryDao
import org.indiv.dls.onerepmax.data.db.ExerciseWithDays
import org.indiv.dls.onerepmax.data.db.IdGenerator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val statsFileReader: StatsFileReader,
    private val statsCalculator: StatsCalculator,
    private val exerciseDao: ExerciseDao,
    private val exerciseDayDao: ExerciseDayDao,
    private val exerciseDayEntryDao: ExerciseDayEntryDao,
    private val idGenerator: IdGenerator
) {
    fun isDarkModeInSettings(): Boolean {
        return sharedPrefsHelper.isDarkMode()
    }

    fun persistDarkMode(dark: Boolean) {
        sharedPrefsHelper.persistDarkMode(dark)
    }




    // TODO add tests for persistence, move persistence out of this class?
    // TODO add ability to add new records from UI
    // TODO update exercise list with new insertions




    suspend fun getExerciseSummaries(): List<ExerciseSummary> {

        // TODO decide how to handle updated data file

        return fetchExerciseSummaries().takeIf { it.isNotEmpty() } ?: run {
            persistAll(loadFromFile())
            fetchExerciseSummaries()
        }
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
