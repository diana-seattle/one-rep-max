package org.indiv.dls.onerepmax.data

import java.time.LocalDate

data class ExerciseWithStats(
    val exerciseName: String,
    val oneRepMaxPersonalRecord: UInt,
    val singleDayResults: List<SingleDayResult>
)

data class SingleDayResult(val date: LocalDate, val oneRepMax: UInt)
