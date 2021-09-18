package org.indiv.dls.onerepmax.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ExerciseDayEntryDao {
    @Insert
    suspend fun insert(exerciseDayEntry: ExerciseDayEntry)
}
