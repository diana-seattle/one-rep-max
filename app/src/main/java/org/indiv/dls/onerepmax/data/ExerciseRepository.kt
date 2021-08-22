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
    private var exerciseDataMap: Map<String, ExerciseWithStats> = emptyMap()

    suspend fun getExerciseSummaries(): List<ExerciseSummary> {
        loadDataIfNeeded()
        return exerciseSummaries
    }

    suspend fun getSingleExerciseData(name: String): ExerciseWithStats? {
        loadDataIfNeeded()
        return exerciseDataMap[name]
    }

    private suspend fun loadDataIfNeeded() {
        if (exerciseSummaries.isEmpty()) {
            val exerciseData = statsCalculator.calculate(statsFileReader.readFile())
            exerciseSummaries = exerciseData.map { it.exerciseSummary }
            exerciseDataMap = exerciseData.map { it.exerciseSummary.exerciseName to it }.toMap()
        }
    }

    fun isDarkModeInSettings(): Boolean {
        return sharedPrefsHelper.isDarkMode()
    }

    fun persistDarkMode(dark: Boolean) {
        sharedPrefsHelper.persistDarkMode(dark)
    }
}
