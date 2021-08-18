package org.indiv.dls.onerepmax.data

import android.content.res.Resources
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.indiv.dls.onerepmax.R
import java.io.InputStream
import com.opencsv.CSVReader
import kotlinx.coroutines.yield
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NumberFormatException
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


data class StatsRecord(
    val dateOfWorkout: LocalDate,
    val exerciseName: String,
    val sets: Int,
    val reps: Int,
    val weight: Int
)

class StatsFileReader(
    private val resources: Resources,
    private val ioCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun readFile(): List<StatsRecord> {
        return withContext(ioCoroutineDispatcher) {
            val inputStream = resources.openRawResource(R.raw.workout_data)
            readInputStream(inputStream)
        }
    }

    suspend fun readInputStream(inputStream: InputStream): List<StatsRecord> {
        val statsRecords = mutableListOf<StatsRecord>()
        var lineNum = 1
        try {
            val reader = CSVReader(InputStreamReader(inputStream))
            var nextLine = reader.readNext()
            while (nextLine != null) {
                statsRecords.add(convert(nextLine, lineNum++))
                yield()
                nextLine = reader.readNext()
            }
            return statsRecords
        } catch (e: IOException) {
            throw RuntimeException("Error reading data file: ${e.message}")
        }
    }

    private fun convert(line: Array<String>, lineNum: Int): StatsRecord {
        try {
            if (line.size != 5) {
                throw RuntimeException("Wrong number of fields")
            }
            return StatsRecord(
                dateOfWorkout = convertDate(line[0]),
                exerciseName = line[1],
                sets = convertInt(line[2]),
                reps = convertInt(line[3]),
                weight = convertInt(line[4])
            )
        } catch (t: Throwable) {
            throw RuntimeException("Error reading data file, line $lineNum: ${t.message}")
        }
    }

    private fun convertInt(s: String): Int {
        return try {
            s.toInt()
        } catch (e: NumberFormatException) {
            throw RuntimeException("Integer value expected but found '$s'")
        }
    }

    private fun convertDate(s: String): LocalDate {
        return try {
            LocalDate.parse(s, DateTimeFormatter.ofPattern("MMM dd yyyy"))
        } catch (e: DateTimeParseException) {
            throw RuntimeException("Date value expected but found '$s'")
        }
    }
}
