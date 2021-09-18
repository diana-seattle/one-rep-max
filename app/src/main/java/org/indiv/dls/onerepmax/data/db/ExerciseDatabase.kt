package org.indiv.dls.onerepmax.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Exercise::class, ExerciseDay::class, ExerciseDayEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class ExerciseDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseDayDao(): ExerciseDayDao
    abstract fun exerciseDayEntryDao(): ExerciseDayEntryDao
}
