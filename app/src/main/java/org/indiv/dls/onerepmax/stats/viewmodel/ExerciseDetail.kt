package org.indiv.dls.onerepmax.stats.viewmodel

import java.time.LocalDate

data class ExerciseDetail(val exercise: Exercise, val dataPoints: List<DataPoint>)

// todo figure out what graphing lib needs
data class DataPoint(val date: LocalDate, val oneRepMax: UInt)
