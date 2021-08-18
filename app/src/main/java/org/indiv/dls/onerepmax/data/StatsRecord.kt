package org.indiv.dls.onerepmax.data

import java.time.LocalDate

data class StatsRecord(
    val dateOfWorkout: LocalDate,
    val exerciseName: String,
    val sets: UInt,
    val reps: UInt,
    val weight: UInt
)

