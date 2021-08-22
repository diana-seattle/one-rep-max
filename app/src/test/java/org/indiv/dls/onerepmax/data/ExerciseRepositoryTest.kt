package org.indiv.dls.onerepmax.data

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ExerciseRepositoryTest {
    companion object {
        private const val exerciseName = "bench press"
        private const val oneRepMax = 250u
        private val statsRecords = listOf(
            StatsRecord(
                dateOfWorkout = LocalDate.now(),
                exerciseName = exerciseName,
                sets = 1u,
                reps = 10u,
                weight = 50u
            )
        )
        private val exercisesWithStats = listOf(ExerciseWithStats(
            exerciseSummary = ExerciseSummary(exerciseName, oneRepMax),
            singleDayResults = listOf(SingleDayResult(LocalDate.now(), oneRepMax))
        ))

    }

    @MockK lateinit var sharedPrefsHelper: SharedPrefsHelper
    @MockK lateinit var statsFileReader: StatsFileReader
    @MockK lateinit var statsCalculator: StatsCalculator

    @InjectMockKs lateinit var exerciseRepository: ExerciseRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        coEvery { statsFileReader.readFile() } returns statsRecords
        coEvery { statsCalculator.calculate(statsRecords) } returns exercisesWithStats
        every { sharedPrefsHelper.persistDarkMode(any()) } just Runs
    }

    @Test
    fun getExerciseSummaries() = runBlocking {
        val results = exerciseRepository.getExerciseSummaries()
        assertEquals(1, results.size)
        assertEquals(exerciseName, results[0].exerciseName)
        assertEquals(oneRepMax, results[0].oneRepMaxPersonalRecord)
    }

    @Test
    fun getSingleExerciseDetail() = runBlocking {
        val result = exerciseRepository.getSingleExerciseDetail(exerciseName)
        assertNotNull(result)
        assertEquals(exerciseName, result!!.exerciseSummary.exerciseName)
        assertEquals(oneRepMax, result.exerciseSummary.oneRepMaxPersonalRecord)
        assertEquals(exercisesWithStats[0].singleDayResults, result.singleDayResults)
    }

    @Test
    fun isDarkModeInSettings_true() {
        every { sharedPrefsHelper.isDarkMode() } returns true
        assertTrue(exerciseRepository.isDarkModeInSettings())
        verify { sharedPrefsHelper.isDarkMode() }
    }

    @Test
    fun isDarkModeInSettings_false() {
        every { sharedPrefsHelper.isDarkMode() } returns false
        assertFalse(exerciseRepository.isDarkModeInSettings())
        verify { sharedPrefsHelper.isDarkMode() }
    }

    @Test
    fun persistDarkMode_true() {
        exerciseRepository.persistDarkMode(true)
        verify { sharedPrefsHelper.persistDarkMode(true) }
    }

    @Test
    fun persistDarkMode_false() {
        exerciseRepository.persistDarkMode(false)
        verify { sharedPrefsHelper.persistDarkMode(false) }
    }
}