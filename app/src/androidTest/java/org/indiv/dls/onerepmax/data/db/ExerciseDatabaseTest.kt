package org.indiv.dls.onerepmax.data.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ExerciseDatabaseTest {
    private lateinit var db: ExerciseDatabase
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var exerciseDayDao: ExerciseDayDao
    private lateinit var exerciseDayEntryDao: ExerciseDayEntryDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ExerciseDatabase::class.java).build()
        exerciseDao = db.exerciseDao()
        exerciseDayDao = db.exerciseDayDao()
        exerciseDayEntryDao = db.exerciseDayEntryDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDao_uniqueName() = runBlocking {
        val exercise1 = createExercise()
        val exercise2 = createExercise()
        exerciseDao.insert(exercise1)
        val exception = try {
            exerciseDao.insert(exercise2)
            null
        } catch (e: java.lang.Exception) {
            e
        }
        assertNotNull(exception)
        assertTrue(exception is SQLiteConstraintException)
        assertTrue(exception!!.message!!.contains("UNIQUE"))
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDao_insertAndGet() = runBlocking {
        val exercise = createExercise()

        exerciseDao.insert(exercise)
        assertEquals(exercise, exerciseDao.getExercise(exercise.name))

        val all = exerciseDao.getAll()
        assertEquals(1, all.size)
        assertEquals(exercise, all[0])
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDao_insertExerciseAndDay() = runBlocking {
        val exercise = createExercise()
        val exerciseDay = createExerciseDay(exercise.id)

        exerciseDao.insert(exercise)
        exerciseDayDao.insert(exerciseDay)
        val exerciseWithDays = exerciseDao.getExerciseWithDays(exercise.id)
        assertNotNull(exerciseWithDays)
        assertEquals(1, exerciseWithDays!!.days.size)
        assertEquals(exerciseDay, exerciseWithDays.days[0])
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDao_update() = runBlocking {
        val exercise = createExercise()
        exerciseDao.insert(exercise)
        exerciseDao.update(exercise.copy(bestOneRepMax = exercise.bestOneRepMax + 1))
        assertEquals(
            exercise.bestOneRepMax + 1,
            exerciseDao.getExercise(exercise.name)!!.bestOneRepMax
        )
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDao_cascadeDelete() = runBlocking {
        val exercise = createExercise()
        val exerciseDay1 = createExerciseDay(exercise.id, date = LocalDate.of(2021, 1, 1))
        val exerciseDay2 = createExerciseDay(exercise.id, date = LocalDate.of(2021, 1, 2))
        val exerciseDay1Entry = createExerciseDayEntry(exerciseDay1.id)
        val exerciseDay2Entry1 = createExerciseDayEntry(exerciseDay2.id)
        val exerciseDay2Entry2 = createExerciseDayEntry(exerciseDay2.id)

        exerciseDao.insert(exercise)
        exerciseDayDao.insert(exerciseDay1)
        exerciseDayDao.insert(exerciseDay2)
        exerciseDayEntryDao.insert(exerciseDay1Entry)
        exerciseDayEntryDao.insert(exerciseDay2Entry1)
        exerciseDayEntryDao.insert(exerciseDay2Entry2)

        exerciseDao.delete(exercise)

        assertTrue(exerciseDao.getAll().isEmpty())
        assertNull(exerciseDayDao.getBestOneRepMax(exercise.id))
        assertNull(exerciseDayEntryDao.getAverageOneRepMax(exerciseDay1.id))
        assertNull(exerciseDayEntryDao.getAverageOneRepMax(exerciseDay2.id))
    }

        @Test
    @Throws(Exception::class)
    fun exerciseDayDao_uniqueExerciseDate() = runBlocking {
        val exercise = createExercise()
        val exerciseDay1 = createExerciseDay(exercise.id)
        val exerciseDay2 = createExerciseDay(exercise.id)

        exerciseDao.insert(exercise)
        exerciseDayDao.insert(exerciseDay1)
        val exception = try {
            exerciseDayDao.insert(exerciseDay2)
            null
        } catch (e: java.lang.Exception) {
            e
        }
        assertNotNull(exception)
        assertTrue(exception is SQLiteConstraintException)
        assertTrue(exception!!.message!!.contains("UNIQUE"))
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDayDao_fk() = runBlocking {
        val exerciseDay = createExerciseDay(UUID.randomUUID().toString())
        val exception = try {
            exerciseDayDao.insert(exerciseDay)
            null
        } catch (e: java.lang.Exception) {
            e
        }
        assertNotNull(exception)
        assertTrue(exception is SQLiteConstraintException)
        assertTrue(exception!!.message!!.contains("FOREIGN KEY"))
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDayDao_insertAndGet() = runBlocking {
        val exercise1 = createExercise(name = "exercise 1")
        val exercise2 = createExercise(name = "exercise 2")
        val exercise1Day = createExerciseDay(exercise1.id)
        val exercise2Day = createExerciseDay(exercise2.id)

        exerciseDao.insert(exercise1)
        exerciseDao.insert(exercise2)
        exerciseDayDao.insert(exercise1Day)
        exerciseDayDao.insert(exercise2Day)

        assertEquals(exercise1Day, exerciseDayDao.getExerciseDay(exercise1.id, exercise1Day.date))
        assertEquals(exercise2Day, exerciseDayDao.getExerciseDay(exercise2.id, exercise2Day.date))
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDayDao_bestOneRepMax() = runBlocking {
        val exercise1 = createExercise(name = "exercise 1")
        val exercise2 = createExercise(name = "exercise 2")
        val exercise1Day = createExerciseDay(exercise1.id, oneRepMax = 50, date = LocalDate.of(2021, 1, 1))
        val exercise2Day1 = createExerciseDay(exercise2.id, oneRepMax = 20, date = LocalDate.of(2021, 1, 1))
        val exercise2Day2 = createExerciseDay(exercise2.id, oneRepMax = 30, date = LocalDate.of(2021, 1, 2))

        exerciseDao.insert(exercise1)
        exerciseDao.insert(exercise2)
        exerciseDayDao.insert(exercise1Day)
        exerciseDayDao.insert(exercise2Day1)
        exerciseDayDao.insert(exercise2Day2)

        assertEquals(50, exerciseDayDao.getBestOneRepMax(exercise1.id))
        assertEquals(30, exerciseDayDao.getBestOneRepMax(exercise2.id))
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDayDao_withEntries() = runBlocking {
        val exercise = createExercise()
        val exerciseDay1 = createExerciseDay(exercise.id, oneRepMax = 20, date = LocalDate.of(2021, 1, 1))
        val exerciseDay2 = createExerciseDay(exercise.id, oneRepMax = 30, date = LocalDate.of(2021, 1, 2))
        val exerciseDay1Entry = createExerciseDayEntry(exerciseDay1.id)
        val exerciseDay2Entry1 = createExerciseDayEntry(exerciseDay2.id)
        val exerciseDay2Entry2 = createExerciseDayEntry(exerciseDay2.id)

        exerciseDao.insert(exercise)
        exerciseDayDao.insert(exerciseDay1)
        exerciseDayDao.insert(exerciseDay2)
        exerciseDayEntryDao.insert(exerciseDay1Entry)
        exerciseDayEntryDao.insert(exerciseDay2Entry1)
        exerciseDayEntryDao.insert(exerciseDay2Entry2)

        val day1Result = exerciseDayDao.getExerciseDayWithEntries(exerciseDay1.id)
        val day2Result = exerciseDayDao.getExerciseDayWithEntries(exerciseDay2.id)

        assertNotNull(day1Result)
        assertEquals(exerciseDay1, day1Result!!.exerciseDay)
        assertEquals(1, day1Result.entries.size)
        assertEquals(exerciseDay1Entry, day1Result.entries[0])

        assertNotNull(day2Result)
        assertEquals(exerciseDay2, day2Result!!.exerciseDay)
        assertEquals(2, day2Result.entries.size)
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDayDao_update() = runBlocking {
        val exercise1 = createExercise(name = "exercise 1")
        val exercise2 = createExercise(name = "exercise 2")
        val exercise1Day = createExerciseDay(exercise1.id, oneRepMax = 50)
        val exercise2Day2 = createExerciseDay(exercise2.id, oneRepMax = 20)

        exerciseDao.insert(exercise1)
        exerciseDao.insert(exercise2)
        exerciseDayDao.insert(exercise1Day)
        exerciseDayDao.insert(exercise2Day2)

        exerciseDayDao.update(exercise1Day.copy(oneRepMax = 30))

        assertEquals(30, exerciseDayDao.getBestOneRepMax(exercise1.id))
        assertEquals(20, exerciseDayDao.getBestOneRepMax(exercise2.id))
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDayEntryDao_fk() = runBlocking {
        val exerciseDayEntry = createExerciseDayEntry(UUID.randomUUID().toString())
        val exception = try {
            exerciseDayEntryDao.insert(exerciseDayEntry)
            null
        } catch (e: java.lang.Exception) {
            e
        }
        assertNotNull(exception)
        assertTrue(exception is SQLiteConstraintException)
        assertTrue(exception!!.message!!.contains("FOREIGN KEY"))
    }

    @Test
    @Throws(Exception::class)
    fun exerciseDayEntryDao_insertAndAverage() = runBlocking {
        val exercise = createExercise()
        val exerciseDay1 = createExerciseDay(exercise.id, date = LocalDate.of(2021, 1, 1))
        val exerciseDay2 = createExerciseDay(exercise.id, date = LocalDate.of(2021, 1, 2))
        val exerciseDay1Entry = createExerciseDayEntry(exerciseDay1.id, oneRepMax = 12.4)
        val exerciseDay2Entry1 = createExerciseDayEntry(exerciseDay2.id, sets = 1, oneRepMax = 10.2)
        val exerciseDay2Entry2 = createExerciseDayEntry(exerciseDay2.id, sets = 2, oneRepMax = 12.4)

        exerciseDao.insert(exercise)
        exerciseDayDao.insert(exerciseDay1)
        exerciseDayDao.insert(exerciseDay2)
        exerciseDayEntryDao.insert(exerciseDay1Entry)
        exerciseDayEntryDao.insert(exerciseDay2Entry1)
        exerciseDayEntryDao.insert(exerciseDay2Entry2)

        assertEquals(12.4, exerciseDayEntryDao.getAverageOneRepMax(exerciseDay1.id)!!, .1)
        assertEquals(11.667, exerciseDayEntryDao.getAverageOneRepMax(exerciseDay2.id)!!, .1)
    }

    private fun createExercise(
        id: String = UUID.randomUUID().toString(),
        name: String = "exercise 1",
        bestOneRepMax: Int = 50
    ): Exercise {
        return Exercise(id, name, bestOneRepMax)
    }

    private fun createExerciseDay(
        exerciseId: String,
        id: String = UUID.randomUUID().toString(),
        date: LocalDate = LocalDate.now(),
        oneRepMax: Int = 50
    ): ExerciseDay {
        return ExerciseDay(id, exerciseId, date, oneRepMax)
    }

    private fun createExerciseDayEntry(
        exerciseDayId: String,
        id: String = UUID.randomUUID().toString(),
        sets: Int = 2,
        reps: Int = 5,
        weight: Int = 15,
        oneRepMax: Double = 50.0
    ): ExerciseDayEntry {
        return ExerciseDayEntry(id, exerciseDayId, sets, reps, weight, oneRepMax)
    }
}