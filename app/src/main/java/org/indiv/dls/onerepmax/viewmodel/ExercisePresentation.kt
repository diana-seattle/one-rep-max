package org.indiv.dls.onerepmax.viewmodel

import org.indiv.dls.onerepmax.uicomponent.ChartView

data class ExercisePresentation(
    val name: String,
    val personalRecord: String
)

data class ExerciseDetailPresentation(
    val exercise: ExercisePresentation,
    val dataPoints: List<ChartView.DataPoint>
)
