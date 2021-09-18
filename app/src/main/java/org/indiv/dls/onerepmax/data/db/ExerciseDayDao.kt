package org.indiv.dls.onerepmax.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ExerciseDayDao {
    @Transaction
    @Query("SELECT * FROM exercise_day WHERE id = :id")
    suspend fun getExerciseDayWithEntries(id: String): ExerciseDayWithEntries?

    @Insert
    suspend fun insert(exerciseDay: ExerciseDay)
}
