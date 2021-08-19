package org.indiv.dls.onerepmax.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.indiv.dls.onerepmax.R
import java.io.InputStream
import com.opencsv.CSVReader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.yield
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NumberFormatException
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsFileReader @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun readFile(): List<StatsRecord> {
        return withContext(Dispatchers.IO) {
            val inputStream = context.resources.openRawResource(R.raw.workout_data)
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
                sets = convertUInt(line[2]),
                reps = convertUInt(line[3]),
                weight = convertUInt(line[4])
            )
        } catch (t: Throwable) {
            throw RuntimeException("Error reading data file, line $lineNum: ${t.message}")
        }
    }

    private fun convertUInt(s: String): UInt {
        return try {
            s.toUInt()
        } catch (e: NumberFormatException) {
            throw RuntimeException("Unsigned integer value expected but found '$s'")
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
