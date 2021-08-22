package org.indiv.dls.onerepmax.data

import java.time.LocalDate

data class ExerciseWithStats(
    val exerciseSummary: ExerciseSummary,
    val singleDayResults: List<SingleDayResult>
)

data class ExerciseSummary(val exerciseName: String, val oneRepMaxPersonalRecord: UInt)

data class SingleDayResult(val date: LocalDate, val oneRepMax: UInt)
