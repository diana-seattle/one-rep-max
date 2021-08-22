package org.indiv.dls.onerepmax.viewmodel

data class ExercisePresentation(
    val name: String,
    val personalRecord: String
)

data class ExerciseDetailPresentation(
    val exercise: ExercisePresentation,
    val dataPoints: List<DataPoint>
)

data class DataPoint(
    val xAxisLabel: String,
    val xAxisValue: Float,
    val yAxisValue: Float
)
