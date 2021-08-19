package org.indiv.dls.onerepmax.stats.viewmodel

import java.time.LocalDate

data class ExerciseDetailPresentation(
    val exercise: ExercisePresentation,
    val dataPoints: List<DataPoint>
)

// todo figure out what graphing lib needs
data class DataPoint(val date: LocalDate, val oneRepMax: UInt)
