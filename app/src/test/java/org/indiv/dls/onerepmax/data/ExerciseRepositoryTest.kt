package org.indiv.dls.onerepmax.data

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.runBlocking
import org.indiv.dls.onerepmax.data.db.Exercise
import org.indiv.dls.onerepmax.data.db.ExerciseDao
import org.indiv.dls.onerepmax.data.db.ExerciseDatabase
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
        private val exerciseWithFullDetails = ExerciseWithFullDetail(
            exerciseName,
            oneRepMax,
            daysWithFullDetail = listOf(DayWithFullDetail(
                date = exerciseDate,
                oneRepMax = oneRepMax,
                calculatedStatsRecords = listOf(CalculatedStatsRecord(
                    statsRecord = statsRecord,
                    oneRepMax = oneRepMaxDecimal
                ))
            ))
        )
        private val exercisesWithFullDetails = listOf(exerciseWithFullDetails)
        private val exerciseEntity = Exercise(exerciseId, exerciseName, oneRepMax.toInt())
        private val exerciseDayEntity = ExerciseDay(exerciseDayId, exerciseId, exerciseDate, oneRepMax.toInt())
        private val exerciseWithDays = ExerciseWithDays(
            exercise = exerciseEntity,
            days = listOf(exerciseDayEntity)
        )
    }

    @MockK lateinit var sharedPrefsHelper: SharedPrefsHelper
    @MockK lateinit var statsFileReader: StatsFileReader
    @MockK lateinit var statsCalculator: StatsCalculator
    @MockK lateinit var exerciseDatabase: ExerciseDatabase
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
        coEvery { statsCalculator.calculateSingleExercise(exerciseName, statsRecords) } returns exerciseWithFullDetails
        coEvery { exerciseDatabase.clearAllTables() } just Runs
        coEvery { exerciseDao.getExercise(statsRecord.exerciseName) } returns exerciseEntity
        coEvery { exerciseDao.getExerciseWithDays(exerciseId) } returns exerciseWithDays
        coEvery { exerciseDao.getAll() } returns listOf(exerciseEntity)
        coEvery { exerciseDao.insert(any()) } just Runs
        coEvery { exerciseDao.update(any()) } just Runs
        coEvery { exerciseDayDao.getExerciseDay(exerciseId, statsRecord.dateOfWorkout) } returns exerciseDayEntity
        coEvery { exerciseDayDao.getBestOneRepMax(any()) } returns oneRepMax.toInt()
        coEvery { exerciseDayDao.insert(any()) } just Runs
        coEvery { exerciseDayDao.update(any()) } just Runs
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
    fun getExerciseSummaries_none() = runBlocking {
        coEvery { exerciseDao.getAll() } returns emptyList()

        val results = exerciseRepository.getExerciseSummaries()

        assertTrue(results.isEmpty())
        coVerifyOrder {
            statsFileReader.readFile()
            statsCalculator.calculate(any())
            exerciseDao.insert(any())
            exerciseDayDao.insert(any())
            exerciseDayEntryDao.insert(any())
        }
        verifyOrder {
            idGenerator.exerciseId()
            idGenerator.exerciseDayId()
            idGenerator.exerciseDayEntryId()
        }
    }

    @Test
    fun resetToFile() = runBlocking {
        exerciseRepository.resetToFile()

        coVerifyOrder {
            exerciseDatabase.clearAllTables()
            statsFileReader.readFile()
            statsCalculator.calculate(any())
            exerciseDao.insert(any())
            exerciseDayDao.insert(any())
            exerciseDayEntryDao.insert(any())
        }
        verifyOrder {
            idGenerator.exerciseId()
            idGenerator.exerciseDayId()
            idGenerator.exerciseDayEntryId()
        }
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
    fun addStatsRecord_newExercise() = runBlocking {
        coEvery { exerciseDao.getExercise(statsRecord.exerciseName) } returns null

        exerciseRepository.addStatsRecord(statsRecord)

        coVerifyOrder {
            exerciseDao.insert(any())
            exerciseDayDao.insert(any())
            exerciseDayEntryDao.insert(any())
        }
    }

    @Test
    fun addStatsRecord_existingExerciseNewDay() = runBlocking {
        coEvery { exerciseDayDao.getExerciseDay(exerciseId, statsRecord.dateOfWorkout) } returns null

        exerciseRepository.addStatsRecord(statsRecord)

        coVerifyOrder {
            exerciseDayDao.insert(any())
            exerciseDayEntryDao.insert(any())
        }
        coVerify(exactly = 0) { exerciseDao.update(any()) }
        verifyOrder {
            idGenerator.exerciseDayId()
            idGenerator.exerciseDayEntryId()
        }
        verify(exactly = 0) { idGenerator.exerciseId() }
    }

    @Test
    fun addStatsRecord_existingExerciseNewDay_newBest() = runBlocking {
        coEvery { exerciseDayDao.getExerciseDay(exerciseId, statsRecord.dateOfWorkout) } returns null
        coEvery {
            exerciseDao.getExercise(statsRecord.exerciseName)
        } returns exerciseEntity.copy(bestOneRepMax = oneRepMax.toInt() - 1)

        exerciseRepository.addStatsRecord(statsRecord)

        coVerifyOrder {
            exerciseDayDao.insert(any())
            exerciseDayEntryDao.insert(any())
            exerciseDao.update(any())
        }
        verifyOrder {
            idGenerator.exerciseDayId()
            idGenerator.exerciseDayEntryId()
        }
        verify(exactly = 0) { idGenerator.exerciseId() }
    }

    @Test
    fun addStatsRecord_existingExerciseExistingDay_noChangeToBest() = runBlocking {
        coEvery { exerciseDayEntryDao.getAverageOneRepMax(any()) } returns oneRepMax.toDouble()

        exerciseRepository.addStatsRecord(statsRecord)

        coVerifyOrder {
            exerciseDayEntryDao.insert(any())
            exerciseDayDao.update(any())
        }
        verify { idGenerator.exerciseDayEntryId() }
    }

    @Test
    fun addStatsRecord_existingExerciseExistingDay_higherBest() = runBlocking {
        val newBest = oneRepMax.toDouble() + 1.1
        coEvery { exerciseDayEntryDao.getAverageOneRepMax(any()) } returns newBest

        exerciseRepository.addStatsRecord(statsRecord)

        coVerifyOrder {
            exerciseDayEntryDao.insert(any())
            exerciseDayDao.update(any())
            exerciseDao.update(any())
        }
        verify { idGenerator.exerciseDayEntryId() }
    }

    @Test
    fun addStatsRecord_existingExerciseExistingDay_lowerBest() = runBlocking {
        val newBest = oneRepMax.toDouble() - 1.1
        coEvery { exerciseDayEntryDao.getAverageOneRepMax(any()) } returns newBest

        exerciseRepository.addStatsRecord(statsRecord)

        coVerifyOrder {
            exerciseDayEntryDao.insert(any())
            exerciseDayDao.update(any())
            exerciseDayDao.getBestOneRepMax(any())
            exerciseDao.update(any())
        }
        verify { idGenerator.exerciseDayEntryId() }
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