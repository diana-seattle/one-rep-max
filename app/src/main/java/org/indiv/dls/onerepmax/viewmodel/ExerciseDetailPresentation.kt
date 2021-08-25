package org.indiv.dls.onerepmax.viewmodel

import org.indiv.dls.onerepmax.uicomponent.ChartView
import org.indiv.dls.onerepmax.uicomponent.ExerciseSummaryView

data class ExerciseDetailPresentation(
    val exerciseSummary: ExerciseSummaryView.Presentation,
    val dataPoints: List<ChartView.DataPoint>
)
