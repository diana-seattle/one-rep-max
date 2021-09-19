package org.indiv.dls.onerepmax.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ExerciseDayEntryDao {
    @Insert
    suspend fun insert(exerciseDayEntry: ExerciseDayEntry)

    @Query("SELECT sum(num_sets * one_rep_max) / sum(num_sets) FROM exercise_day_entry where exercise_day_id = :exerciseDayId")
    suspend fun getAverageOneRepMax(exerciseDayId: String): Double?
}
