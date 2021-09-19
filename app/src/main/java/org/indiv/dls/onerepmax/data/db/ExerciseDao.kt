package org.indiv.dls.onerepmax.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    suspend fun getAll(): List<Exercise>

    @Query("SELECT * FROM exercise WHERE exercise_name = :exerciseName")
    suspend fun getExercise(exerciseName: String): Exercise?

    @Transaction
    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getExerciseWithDays(id: String): ExerciseWithDays?

    @Insert
    suspend fun insert(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}
