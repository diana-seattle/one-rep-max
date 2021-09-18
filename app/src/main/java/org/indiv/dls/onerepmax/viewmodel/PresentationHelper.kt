package org.indiv.dls.onerepmax.viewmodel

import org.indiv.dls.onerepmax.data.ExerciseSummary
import org.indiv.dls.onerepmax.data.ExerciseWithStats
import org.indiv.dls.onerepmax.uicomponent.ChartView
import org.indiv.dls.onerepmax.uicomponent.ExerciseSummaryView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class PresentationHelper @Inject constructor() {

    companion object {
        private val monthDayFormatter = DateTimeFormatter.ofPattern("MMM d")
    }

    fun getExercises(exerciseData: List<ExerciseSummary>): List<ExerciseSummaryView.Presentation> {
        return exerciseData.map { ExerciseSummaryView.Presentation(
            exerciseId = it.exerciseId,
            name = it.exerciseName,
            personalRecord = it.oneRepMaxPersonalRecord.toString()
        ) }
    }

    fun getExerciseDetail(exerciseWithStats: ExerciseWithStats): ExerciseDetailPresentation {
        val sortedResults = exerciseWithStats.singleDayResults.sortedBy { it.date }
        val dataPoints = sortedResults.mapIndexed { index, result ->
            ChartView.DataPoint(
                xAxisLabel = formatDateLabel(result.date),
                xAxisValue = index.toFloat(),
                yAxisValue = result.oneRepMax.toFloat()
            )
        }

        return ExerciseDetailPresentation(
            ExerciseSummaryView.Presentation(
                exerciseWithStats.exerciseSummary.exerciseId,
                exerciseWithStats.exerciseSummary.exerciseName,
                exerciseWithStats.exerciseSummary.oneRepMaxPersonalRecord.toString()
            ),
            dataPoints
        )
    }

    private fun formatDateLabel(date: LocalDate): String {
        return monthDayFormatter.format(date)
    }
}
