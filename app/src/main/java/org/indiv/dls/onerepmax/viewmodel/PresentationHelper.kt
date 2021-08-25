package org.indiv.dls.onerepmax.viewmodel

import org.indiv.dls.onerepmax.data.ExerciseSummary
import org.indiv.dls.onerepmax.data.ExerciseWithStats
import org.indiv.dls.onerepmax.uicomponent.ChartView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class PresentationHelper @Inject constructor() {

    companion object {
        private val monthDayFormatter = DateTimeFormatter.ofPattern("MMM d")
    }

    fun getExercises(exerciseData: List<ExerciseSummary>): List<ExercisePresentation> {
        return exerciseData.map { ExercisePresentation(it.exerciseName, it.oneRepMaxPersonalRecord.toString()) }
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
            ExercisePresentation(
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
