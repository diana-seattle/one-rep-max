package org.indiv.dls.onerepmax.data.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateConverter {
    companion object {
        private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    }

    @TypeConverter
    fun fromDateString(dateString: String): LocalDate {
        return LocalDate.parse(dateString, localDateFormatter)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return localDateFormatter.format(date)
    }
}
