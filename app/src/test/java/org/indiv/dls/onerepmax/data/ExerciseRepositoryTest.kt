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
import org.indiv.dls.onerepmax.data.db.Exercise
import org.indiv.dls.onerepmax.data.db.ExerciseDao
import org.indiv.dls.onerepmax.data.db.ExerciseDay
import org.indiv.dls.onerepmax.data.db.ExerciseDayDao
import org.indiv.dls.onerepmax.data.db.ExerciseDayEntryDao
import org.indiv.dls.onerepmax.data.db.ExerciseWithDays
import org.indiv.dls.onerepmax.data.db.IdGenerator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ExerciseRepositoryTest {
    companion object {
        private const val exerciseId = "123"
        private const val exerciseDayId = "456"
        private const val exerciseDayEntryId = "789"
        private const val exerciseName = "bench press"
        private const val oneRepMaxDecimal = 250.1
        private val oneRepMax = oneRepMaxDecimal.toUInt()
        private val exerciseDate = LocalDate.now()
        private val statsRecord = StatsRecord(
            dateOfWorkout = exerciseDate,
            exerciseName = exerciseName,
            sets = 1u,
            reps = 10u,
            weight = 50u
        )
        private val statsRecords = listOf(statsRecord)
        private val exercisesWithStats = listOf(ExerciseWithStats(
            exerciseSummary = ExerciseSummary(exerciseId, exerciseName, oneRepMax),
            singleDayResults = listOf(SingleDayResult(exerciseDate, oneRepMax))
        ))
        private val exercisesWithFullDetails = listOf(ExerciseWithFullDetail(
            exerciseName, oneRepMax,
            daysWithFullDetail = listOf(DayWithFullDetail(
                date = exerciseDate,
                oneRepMax = oneRepMax,
                calculatedStatsRecords = listOf(CalculatedStatsRecord(
                    statsRecord = statsRecord,
                    oneRepMax = oneRepMaxDecimal
                ))
            ))
        ))
        private val exerciseEntity = Exercise(exerciseId, exerciseName, oneRepMax.toInt())
        private val exerciseWithDays = ExerciseWithDays(
            exercise = exerciseEntity,
            days = listOf(ExerciseDay(
                id = exerciseDayId,
                exerciseId = exerciseId,
                date = exerciseDate,
                oneRepMax = oneRepMax.toInt()
            ))
        )
    }

    @MockK lateinit var sharedPrefsHelper: SharedPrefsHelper
    @MockK lateinit var statsFileReader: StatsFileReader
    @MockK lateinit var statsCalculator: StatsCalculator
    @MockK lateinit var exerciseDao: ExerciseDao
    @MockK lateinit var exerciseDayDao: ExerciseDayDao
    @MockK lateinit var exerciseDayEntryDao: ExerciseDayEntryDao
    @MockK lateinit var idGenerator: IdGenerator

    @InjectMockKs lateinit var exerciseRepository: ExerciseRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        coEvery { statsFileReader.readFile() } returns statsRecords
        coEvery { statsCalculator.calculate(statsRecords) } returns exercisesWithFullDetails
        coEvery { exerciseDao.getExerciseWithDays(exerciseId) } returns exerciseWithDays
        coEvery { exerciseDao.getAll() } returns listOf(exerciseEntity)
        coEvery { exerciseDao.insert(any()) } just Runs
        coEvery { exerciseDayDao.insert(any()) } just Runs
        coEvery { exerciseDayEntryDao.insert(any()) } just Runs
        every { idGenerator.exerciseId() } returns exerciseId
        every { idGenerator.exerciseDayId() } returns exerciseDayId
        every { idGenerator.exerciseDayEntryId() } returns exerciseDayEntryId
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
        val result = exerciseRepository.getSingleExerciseDetail(exerciseId)
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