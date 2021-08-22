package org.indiv.dls.onerepmax.viewmodel

data class ExerciseDetailPresentation(
    val exerciseSummary: ExercisePresentation,
    val dataPoints: List<DataPoint>
)

data class DataPoint(
    val xAxisLabel: String,
    val xAxisValue: Float,
    val yAxisValue: Float
)