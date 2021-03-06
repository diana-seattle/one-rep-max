package org.indiv.dls.onerepmax.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.math.roundToInt

class StatsCalculatorTest {
    companion object {
        private val day1 = LocalDate.of(2020, 1, 2)
        private val day2 = LocalDate.of(2020, 2, 3)
        private val day3 = LocalDate.of(2020, 3, 4)
    }

    private lateinit var statsCalculator: StatsCalculator

    @Before
    fun setUp() {
        statsCalculator = StatsCalculator()
    }

    @Test
    fun calculate_empty() = runBlocking {
        val results = statsCalculator.calculate(emptyList())
        assertTrue(results.isEmpty())
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
        assertEquals(2, exerciseResult.daysWithFullDetail.size) // 2 days
        assertEquals(31u, exerciseResult.oneRepMaxPersonalRecord)
        val day1Result = exerciseResult.daysWithFullDetail[0]
        val day2Result = exerciseResult.daysWithFullDetail[1]
        assertEquals(31u, day1Result.oneRepMax)
        assertEquals(28u, day2Result.oneRepMax)
        assertEquals(day1, day1Result.date)
        assertEquals(day2, day2Result.date)
        assertEquals(2, day1Result.calculatedStatsRecords.size)
        assertEquals(1, day2Result.calculatedStatsRecords.size)
        assertEquals(28, day2Result.calculatedStatsRecords[0].oneRepMax.roundToInt())
    }

    @Test
    fun calculate_multipleExercises_oneDay() = runBlocking {
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
        assertEquals(1, exercise1Result.daysWithFullDetail.size) // 1 day
        assertEquals(1, exercise2Result.daysWithFullDetail.size) // 1 day
        assertEquals(1, exercise3Result.daysWithFullDetail.size) // 1 day
        assertEquals(44u, exercise1Result.oneRepMaxPersonalRecord)
        assertEquals(25u, exercise2Result.oneRepMaxPersonalRecord)
        assertEquals(28u, exercise3Result.oneRepMaxPersonalRecord)
        assertEquals(44u, exercise1Result.daysWithFullDetail[0].oneRepMax)
        assertEquals(25u, exercise2Result.daysWithFullDetail[0].oneRepMax)
        assertEquals(28u, exercise3Result.daysWithFullDetail[0].oneRepMax)
        assertEquals(1, exercise1Result.daysWithFullDetail[0].calculatedStatsRecords.size)
        assertEquals(1, exercise2Result.daysWithFullDetail[0].calculatedStatsRecords.size)
        assertEquals(1, exercise3Result.daysWithFullDetail[0].calculatedStatsRecords.size)
        assertEquals(44, exercise1Result.daysWithFullDetail[0].calculatedStatsRecords[0].oneRepMax.roundToInt())
        assertEquals(25, exercise2Result.daysWithFullDetail[0].calculatedStatsRecords[0].oneRepMax.roundToInt())
        assertEquals(28, exercise3Result.daysWithFullDetail[0].calculatedStatsRecords[0].oneRepMax.roundToInt())
    }

    @Test
    fun calculate_avoidingDivideByZero() = runBlocking {
        val records = listOf(
            createStatsRecord().copy(dateOfWorkout = day1, reps = 36u, weight = 1u),
            createStatsRecord().copy(dateOfWorkout = day2, reps = 37u, weight = 1u),
            createStatsRecord().copy(dateOfWorkout = day3, reps = 100u, weight = 1u)
        )

        val results = statsCalculator.calculate(records)

        assertEquals(1, results.size) // one exercise
        val exerciseResult = results[0]
        assertEquals(3, exerciseResult.daysWithFullDetail.size) // 3 days

        assertEquals(36u, exerciseResult.oneRepMaxPersonalRecord)
        exerciseResult.daysWithFullDetail.forEach {
            assertEquals(36u, it.oneRepMax)
        }
    }

    @Test
    fun calculateSingleExercise_multipleDays() = runBlocking {
        val records = listOf(
            createStatsRecord().copy(dateOfWorkout = day1, sets = 1u, reps = 4u, weight = 40u),
            createStatsRecord().copy(dateOfWorkout = day1, sets = 2u, reps = 8u, weight = 20u),
            createStatsRecord().copy(dateOfWorkout = day2, sets = 1u, reps = 5u, weight = 25u)
        )

        val exerciseResult = statsCalculator.calculateSingleExercise(records[0].exerciseName, records)

        assertEquals(2, exerciseResult.daysWithFullDetail.size) // 2 days
        assertEquals(31u, exerciseResult.oneRepMaxPersonalRecord)
        val day1Result = exerciseResult.daysWithFullDetail[0]
        val day2Result = exerciseResult.daysWithFullDetail[1]
        assertEquals(31u, day1Result.oneRepMax)
        assertEquals(28u, day2Result.oneRepMax)
        assertEquals(day1, day1Result.date)
        assertEquals(day2, day2Result.date)
        assertEquals(2, day1Result.calculatedStatsRecords.size)
        assertEquals(1, day2Result.calculatedStatsRecords.size)
        assertEquals(28, day2Result.calculatedStatsRecords[0].oneRepMax.roundToInt())
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