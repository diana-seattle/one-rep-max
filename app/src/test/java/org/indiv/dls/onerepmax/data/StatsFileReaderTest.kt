package org.indiv.dls.onerepmax.data

import android.content.res.Resources
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.lang.Exception
import java.lang.RuntimeException
import java.time.LocalDate


class StatsFileReaderTest {
    companion object {
        private val line1 = "Oct 11 2020,Back Squat,1,10,45\n"
        private val record1 = StatsRecord(
            dateOfWorkout = LocalDate.of(2020, 10, 11),
            exerciseName = "Back Squat",
            sets = 1u,
            reps = 10u,
            weight = 45u
        )

        private val line2 = "Apr 28 2019,Barbell Bench Press,2,4,140\n"
        private val record2 = StatsRecord(
            dateOfWorkout = LocalDate.of(2019, 4, 28),
            exerciseName = "Barbell Bench Press",
            sets = 2u,
            reps = 4u,
            weight = 140u
        )

        private val line3 = "Jul 15 2021,Deadlift,1,5,245\n"
        private val record3 = StatsRecord(
            dateOfWorkout = LocalDate.of(2021, 7, 15),
            exerciseName = "Deadlift",
            sets = 1u,
            reps = 5u,
            weight = 245u
        )

        private const val incompleteLine = "Jul 15 2021,Deadlift,1,5\n"
        private const val badIntFormatLine = "Jul 15 2021,Deadlift,1b,5,245\n"
        private const val negativeIntFormatLine = "Jul 15 2021,Deadlift,1,-5,245\n"
        private const val badDateFormatLine = "Whenever 15 2021,Deadlift,1b,5,245\n"
    }

    @MockK lateinit var resources: Resources

    private lateinit var statsFileReader: StatsFileReader

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        statsFileReader = StatsFileReader(resources, TestCoroutineDispatcher())
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
        val results = statsFileReader.readInputStream(inputStream)

        assertEquals(1, results.size)
        assertEquals(record1, results[0])
    }

    @Test
    fun readInputStream_multipleLines() = runBlocking {
        val inputStream = createInputStream(line1 + line2 + line3)
        val results = statsFileReader.readInputStream(inputStream)

        assertEquals(3, results.size)
        assertEquals(record1, results[0])
        assertEquals(record2, results[1])
        assertEquals(record3, results[2])
    }

    // TODO: migrate to junit 5 which makes testing for exceptions easier
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
            assertEquals("Error reading data file, line 1: Unsigned integer value expected but found '1b'", e.message)
            throw e
        }
    }

    @Test(expected = RuntimeException::class)
    fun readInputStream_negativeIntFormat(): Unit = runBlocking {
        val inputStream = createInputStream(line1 + negativeIntFormatLine + line3)
        try {
            statsFileReader.readInputStream(inputStream)
        } catch (e: Exception) {
            assertEquals("Error reading data file, line 2: Unsigned integer value expected but found '-5'", e.message)
            throw e
        }
    }

    private fun createInputStream(s: String): InputStream {
        return ByteArrayInputStream(s.encodeToByteArray())
    }
}
