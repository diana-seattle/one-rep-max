package org.indiv.dls.onerepmax.stats.viewmodel

import org.indiv.dls.onerepmax.data.ExerciseWithStats
import org.indiv.dls.onerepmax.data.SingleDayResult
import org.junit.Assert.*

import org.junit.Test
import java.time.LocalDate

class PresentationHelperTest {

    private val presentationHelper = PresentationHelper()

    @Test
    fun getExercises() {
        val input = listOf(
            createExerciseWithStats(exerciseName = "exercise1", oneRepMaxPersonalRecord = 15u),
            createExerciseWithStats(exerciseName = "exercise2", oneRepMaxPersonalRecord = 12u)
        )

        val results = presentationHelper.getExercises(input)

        assertEquals(2, results.size)
        assertEquals("exercise1", results[0].name)
        assertEquals("exercise2", results[1].name)
        assertEquals("15", results[0].personalRecord)
        assertEquals("12", results[1].personalRecord)
    }

    @Test
    fun getExerciseDetail() {
        val input = createExerciseWithStats()

        val result = presentationHelper.getExerciseDetail(input)

        assertEquals(input.exerciseName, result.exercise.name)
        assertEquals(input.oneRepMaxPersonalRecord.toString(), result.exercise.personalRecord)
        assertEquals(input.singleDayResults.size, result.dataPoints.size)
        result.dataPoints.forEachIndexed { i, dataPoint ->
            assertEquals(input.singleDayResults[i].date, dataPoint.date)
            assertEquals(input.singleDayResults[i].oneRepMax, dataPoint.oneRepMax)
        }
    }

    private fun createExerciseWithStats(
        exerciseName: String = "exercise1",
        oneRepMaxPersonalRecord: UInt = 15u,
        singleDayResults: List<SingleDayResult> = listOf(
            SingleDayResult(LocalDate.now(), 5u),
            SingleDayResult(LocalDate.now(), 15u)
        )
    ): ExerciseWithStats {
        return ExerciseWithStats(
            exerciseName = exerciseName,
            oneRepMaxPersonalRecord = oneRepMaxPersonalRecord,
            singleDayResults = singleDayResults
        )
    }
}
