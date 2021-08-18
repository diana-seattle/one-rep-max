package org.indiv.dls.onerepmax.data

import android.content.res.Resources
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.io.InputStream
import android.R.string
import java.io.ByteArrayInputStream
import java.lang.Exception
import java.lang.RuntimeException
import java.time.LocalDate


class StatsFileReaderTest {
    companion object {
        val line1 = "Oct 11 2020,Back Squat,1,10,45\n"
        val record1 = StatsRecord(
            dateOfWorkout = LocalDate.of(2020, 10, 11),
            exerciseName = "Back Squat",
            sets = 1,
            reps = 10,
            weight = 45
        )

        val line2 = "Apr 28 2019,Barbell Bench Press,2,4,140\n"
        val record2 = StatsRecord(
            dateOfWorkout = LocalDate.of(2019, 4, 28),
            exerciseName = "Barbell Bench Press",
            sets = 2,
            reps = 4,
            weight = 140
        )

        val line3 = "Jul 15 2021,Deadlift,1,5,245\n"
        val record3 = StatsRecord(
            dateOfWorkout = LocalDate.of(2021, 7, 15),
            exerciseName = "Deadlift",
            sets = 1,
            reps = 5,
            weight = 245
        )

        val incompleteLine = "Jul 15 2021,Deadlift,1,5\n"
        val badIntFormatLine = "Jul 15 2021,Deadlift,1b,5,245\n"
        val badDateFormatLine = "Whenever 15 2021,Deadlift,1b,5,245\n"
    }

    // See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @MockK lateinit var resources: Resources

    private lateinit var statsFileReader: StatsFileReader

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        statsFileReader = StatsFileReader(resources, testCoroutineDispatcher)
    }

    @Test
    fun readInputStream_empty() = runBlocking {
        val inputStream = createInputStream("")
        val result = statsFileReader.readInputStream(inputStream)
        assertTrue(result.isEmpty())
    }

    @Test
    fun readInputStream_oneLine() = runBlocking {
        val inputStream = createInputStream(line1)
        val result = statsFileReader.readInputStream(inputStream)

        assertEquals(1, result.size)
        assertEquals(record1, result[0])
    }

    @Test
    fun readInputStream_multipleLines() = runBlocking {
        val inputStream = createInputStream(line1 + line2 + line3)
        val result = statsFileReader.readInputStream(inputStream)

        assertEquals(3, result.size)
        assertEquals(record1, result[0])
        assertEquals(record2, result[1])
        assertEquals(record3, result[2])
    }

    @Test(expected = RuntimeException::class)
    fun readInputStream_incompleteLine(): Unit = runBlocking {
        val inputStream = createInputStream(line1 + incompleteLine + line3)
        try {
            statsFileReader.readInputStream(inputStream)
        } catch (e: Exception) {
            assertEquals("Error reading data file, line 2: Wrong number of fields", e.message)
            throw e
        }
    }

    @Test(expected = RuntimeException::class)
    fun readInputStream_badDateFormat(): Unit = runBlocking {
        val inputStream = createInputStream(line1 + line2 + line3 + badDateFormatLine)
        try {
            statsFileReader.readInputStream(inputStream)
        } catch (e: Exception) {
            assertEquals("Error reading data file, line 4: Date value expected but found 'Whenever 15 2021'", e.message)
            throw e
        }
    }

    @Test(expected = RuntimeException::class)
    fun readInputStream_badIntFormat(): Unit = runBlocking {
        val inputStream = createInputStream(badIntFormatLine + line2 + line3)
        try {
            statsFileReader.readInputStream(inputStream)
        } catch (e: Exception) {
            assertEquals("Error reading data file, line 1: Integer value expected but found '1b'", e.message)
            throw e
        }
    }

    private fun createInputStream(s: String): InputStream {
        return ByteArrayInputStream(s.encodeToByteArray())
    }
}
