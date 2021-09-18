package org.indiv.dls.onerepmax.data

import java.time.LocalDate

data class ExerciseWithFullDetail(
    val exerciseName: String,
    val oneRepMaxPersonalRecord: UInt,
    val daysWithFullDetail: List<DayWithFullDetail>
)

data class DayWithFullDetail(
    val date: LocalDate,
    val oneRepMax: UInt,
    val calculatedStatsRecords: List<CalculatedStatsRecord>
)

data class CalculatedStatsRecord(
    val statsRecord: StatsRecord,
    val oneRepMax: Double
)
