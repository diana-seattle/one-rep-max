package org.indiv.dls.onerepmax.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val statsFileReader: StatsFileReader,
    private val statsCalculator: StatsCalculator
) {
    // In-memory copy of all the data.
    // TODO: Consider loading into a Room database.
    private var exerciseSummaries: List<ExerciseSummary> = emptyList()
    private var exerciseDetailMap: Map<String, ExerciseWithStats> = emptyMap()

    suspend fun getExerciseSummaries(): List<ExerciseSummary> {
        loadDataIfNeeded()
        return exerciseSummaries
    }

    suspend fun getSingleExerciseDetail(name: String): ExerciseWithStats? {
        loadDataIfNeeded()
        return exerciseDetailMap[name]
    }

    private suspend fun loadDataIfNeeded() {
        if (exerciseSummaries.isEmpty()) {
            val statsRecords = statsFileReader.readFile()
            val exerciseData = statsCalculator.calculate(statsRecords)
            exerciseSummaries = exerciseData.map { it.exerciseSummary }
            exerciseDetailMap = exerciseData.map { it.exerciseSummary.exerciseName to it }.toMap()
        }
    }

    fun isDarkModeInSettings(): Boolean {
        return sharedPrefsHelper.isDarkMode()
    }

    fun persistDarkMode(dark: Boolean) {
        sharedPrefsHelper.persistDarkMode(dark)
    }
}
