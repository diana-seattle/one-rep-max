package org.indiv.dls.onerepmax.viewmodel

import org.indiv.dls.onerepmax.data.ExerciseWithStats
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class PresentationHelper @Inject constructor() {

    companion object {
        private val monthDayFormatter = DateTimeFormatter.ofPattern("MMM d")
    }

    fun getExercises(exerciseData: List<ExerciseWithStats>): List<ExercisePresentation> {
        return exerciseData.map { ExercisePresentation(it.exerciseName, it.oneRepMaxPersonalRecord.toString()) }
    }

    fun getExerciseDetail(exerciseWithStats: ExerciseWithStats): ExerciseDetailPresentation {
        val sortedResults = exerciseWithStats.singleDayResults.sortedBy { it.date }
        val dataPoints = sortedResults.mapIndexed { index, result ->
            DataPoint(
                xAxisLabel = formatDateLabel(result.date),
                xAxisValue = index.toFloat(),
                yAxisValue = result.oneRepMax.toFloat()
            )
        }

        return ExerciseDetailPresentation(
            ExercisePresentation(
                exerciseWithStats.exerciseName,
                exerciseWithStats.oneRepMaxPersonalRecord.toString()
            ),
            dataPoints
        )
    }

    private fun formatDateLabel(date: LocalDate): String {
        return monthDayFormatter.format(date)
    }
}
