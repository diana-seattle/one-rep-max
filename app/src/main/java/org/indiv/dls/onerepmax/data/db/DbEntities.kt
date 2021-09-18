package org.indiv.dls.onerepmax.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

/**
 * Exercise parent table
 */
@Entity(
    tableName = "exercise",
    indices = [Index(value = ["exercise_name"], unique = true)]
)
data class Exercise(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "exercise_name") val name: String,
    @ColumnInfo(name = "best_one_rep_max") val bestOneRepMax: Int
)


/**
 * Exercise Day table - represents one day of one exercise
 */
@Entity(
    tableName = "exercise_day",
    indices = [Index(value = ["exercise_id", "exercise_date"], unique = true)],
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = ["id"],
        childColumns = ["exercise_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ExerciseDay(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "exercise_id", index = true) val exerciseId: String,
    @ColumnInfo(name = "exercise_date") val date: LocalDate,
    @ColumnInfo(name = "one_rep_max") val oneRepMax: Int
)


/**
 * Exercise Day Entry table - represents one entry on one day of one exercise
 */
@Entity(
    tableName = "exercise_day_entry",
    foreignKeys = [ForeignKey(
        entity = ExerciseDay::class,
        parentColumns = ["id"],
        childColumns = ["exercise_day_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ExerciseDayEntry(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "exercise_day_id", index = true) val exerciseDayId: String,
    @ColumnInfo(name = "num_sets") val sets: Int,
    @ColumnInfo(name = "num_reps") val reps: Int,
    @ColumnInfo(name = "weight") val weight: Int,
    @ColumnInfo(name = "one_rep_max") val oneRepMax: Double
)


data class ExerciseWithDays(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "id",
        entityColumn = "exercise_id"
    )
    val days: List<ExerciseDay>
)


data class ExerciseDayWithEntries(
    @Embedded val exerciseDay: ExerciseDay,
    @Relation(
        parentColumn = "id",
        entityColumn = "exercise_day_id"
    )
    val days: List<ExerciseDayEntry>
)
