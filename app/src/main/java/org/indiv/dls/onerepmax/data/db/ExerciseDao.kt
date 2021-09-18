package org.indiv.dls.onerepmax.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    suspend fun getAll(): List<Exercise>

    @Transaction
    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getExerciseWithDays(id: String): ExerciseWithDays?

    @Insert
    suspend fun insert(exercise: Exercise)
}
