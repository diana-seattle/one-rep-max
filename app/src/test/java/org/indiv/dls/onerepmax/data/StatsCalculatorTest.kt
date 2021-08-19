package org.indiv.dls.onerepmax.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class StatsCalculatorTest {
    companion object {
        private val day1 = LocalDate.of(2020, 1, 2)
        private val day2 = LocalDate.of(2020, 2, 3)
    }

    private lateinit var statsCalculator: StatsCalculator

    @Before
    fun setUp() {
        statsCalculator = StatsCalculator()
    }

    @Test
    fun calculate_oneExercise_multipleDays() = runBlocking {
        val records = listOf(
            createStatsRecord().copy(dateOfWorkout = day1, sets = 1u, reps = 4u, weight = 40u),
            createStatsRecord().copy(dateOfWorkout = day1, sets = 2u, reps = 8u, weight = 20u),
            createStatsRecord().copy(dateOfWorkout = day2, sets = 1u, reps = 5u, weight = 25u)
        )

        val results = statsCalculator.calculate(records)

        assertEquals(1, results.size) // one exercise
        val exerciseResult = results[0]
        assertEquals(2, exerciseResult.singleDayResults.size) // 2 days
        assertEquals(31u, exerciseResult.oneRepMaxPersonalRecord)
        val day1Result = exerciseResult.singleDayResults[0]
        val day2Result = exerciseResult.singleDayResults[1]
        assertEquals(31u, day1Result.oneRepMax)
        assertEquals(28u, day2Result.oneRepMax)
        assertEquals(day1, day1Result.date)
        assertEquals(day2, day2Result.date)
    }

    @Test
    fun calculate_multipleExercises() = runBlocking {
        val records = listOf(
            createStatsRecord().copy(exerciseName = "e1", reps = 4u, weight = 40u),
            createStatsRecord().copy(exerciseName = "e2", reps = 8u, weight = 20u),
            createStatsRecord().copy(exerciseName = "e3", reps = 5u, weight = 25u)
        )

        val results = statsCalculator.calculate(records)

        assertEquals(3, results.size) // 3 exercises
        val exercise1Result = results[0]
        val exercise2Result = results[1]
        val exercise3Result = results[2]
        assertEquals(1, exercise1Result.singleDayResults.size) // 1 day
        assertEquals(1, exercise2Result.singleDayResults.size) // 1 day
        assertEquals(1, exercise3Result.singleDayResults.size) // 1 day
        assertEquals(44u, exercise1Result.oneRepMaxPersonalRecord)
        assertEquals(25u, exercise2Result.oneRepMaxPersonalRecord)
        assertEquals(28u, exercise3Result.oneRepMaxPersonalRecord)
        assertEquals(44u, exercise1Result.singleDayResults[0].oneRepMax)
        assertEquals(25u, exercise2Result.singleDayResults[0].oneRepMax)
        assertEquals(28u, exercise3Result.singleDayResults[0].oneRepMax)
    }

    private fun createStatsRecord(
        dateOfWorkout: LocalDate = LocalDate.now(),
        exerciseName: String = "Test Exercise",
        sets: UInt = 1u,
        reps: UInt = 10u,
        weight: UInt = 50u
    ): StatsRecord {
        return StatsRecord(
            dateOfWorkout,
            exerciseName,
            sets,
            reps,
            weight
        )
    }
}