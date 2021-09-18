package org.indiv.dls.onerepmax.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import java.time.LocalDate

@Dao
interface ExerciseDayDao {
    @Query("SELECT * FROM exercise_day WHERE exercise_id = :exerciseId AND exercise_date = :dateOfWorkout")
    suspend fun getExerciseDay(exerciseId: String, dateOfWorkout: LocalDate): ExerciseDay?

    @Query("SELECT max(one_rep_max) FROM exercise_day WHERE exercise_id = :exerciseId")
    suspend fun getBestOneRepMax(exerciseId: String): Int

    @Transaction
    @Query("SELECT * FROM exercise_day WHERE id = :id")
    suspend fun getExerciseDayWithEntries(id: String): ExerciseDayWithEntries?

    @Insert
    suspend fun insert(exerciseDay: ExerciseDay)

    @Update
    suspend fun update(exerciseDay: ExerciseDay)
}
