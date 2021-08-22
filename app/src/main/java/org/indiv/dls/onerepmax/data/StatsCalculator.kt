package org.indiv.dls.onerepmax.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class StatsCalculator @Inject constructor() {

    companion object {
        private const val MAX_ALLOWED_REPS = 36u
    }

    suspend fun calculate(records: List<StatsRecord>): List<ExerciseWithStats> {
        return withContext(Dispatchers.Default) {
            records.groupBy { it.exerciseName }.map {
                yield()
                calculateSingleExercise(exerciseName = it.key, records = it.value)
            }
        }
    }

    private fun calculateSingleExercise(exerciseName: String, records: List<StatsRecord>): ExerciseWithStats {
        val singleDayResults = records.groupBy { it.dateOfWorkout }.map {
            SingleDayResult(date = it.key, oneRepMax = calculateSingleDay(singleDayRecords = it.value))
        }
        val personalRecord = singleDayResults.maxOf { it.oneRepMax }
        return ExerciseWithStats(ExerciseSummary(exerciseName, personalRecord), singleDayResults)
    }

    private fun calculateSingleDay(singleDayRecords: List<StatsRecord>): UInt {
        // Even though the sets value may always be one, playing it safe here and using it as a multiplier.
        val sumOfValues = singleDayRecords.map { it.sets.toDouble() * brzycki(it.weight, it.reps) }
            .sum()
        val countOfValues = singleDayRecords.map { it.sets }
            .sum()
        return average(sumOfValues, countOfValues)
    }

    private fun average(sumOfValues: Double, countOfValues: UInt): UInt {
        return if (countOfValues == 0u) {
            0u
        } else
            (sumOfValues / countOfValues.toDouble()).roundToInt().toUInt()
    }

    private fun brzycki(weight: UInt, reps: UInt): Double {
        // Brzycki calc: https://en.wikipedia.org/wiki/One-repetition_maximum

        // Anything larger than MAX_ALLOWED_REPS will cause a divide-by-zero exception or a negative result.
        // TODO: confirm with product management what they want the behavior to be with high reps.
        val safeReps = reps.coerceAtMost(MAX_ALLOWED_REPS)
        return ((weight * 36u).toDouble() / (37u - safeReps).toDouble())
    }
}
