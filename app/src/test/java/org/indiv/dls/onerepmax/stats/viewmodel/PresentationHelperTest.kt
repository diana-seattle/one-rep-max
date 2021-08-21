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

        assertEquals(input.exerciseName, result.exerciseSummary.name)
        assertEquals(input.oneRepMaxPersonalRecord.toString(), result.exerciseSummary.personalRecord)
        assertEquals(input.singleDayResults.size, result.dataPoints.size)
        result.dataPoints.forEachIndexed { i, dataPoint ->
            assertEquals(i.toFloat(), dataPoint.xAxisValue)
            assertEquals(input.singleDayResults[i].oneRepMax.toFloat(), dataPoint.yAxisValue)
        }
        assertEquals("May 12 2020", result.dataPoints[0].xAxisLabel)
        assertEquals("Dec 14 2020", result.dataPoints[1].xAxisLabel)
    }

    private fun createExerciseWithStats(
        exerciseName: String = "exercise1",
        oneRepMaxPersonalRecord: UInt = 15u,
        singleDayResults: List<SingleDayResult> = listOf(
            SingleDayResult(LocalDate.of(2020, 5, 12), 5u),
            SingleDayResult(LocalDate.of(2020, 12, 14), 15u)
        )
    ): ExerciseWithStats {
        return ExerciseWithStats(
            exerciseName = exerciseName,
            oneRepMaxPersonalRecord = oneRepMaxPersonalRecord,
            singleDayResults = singleDayResults
        )
    }
}
