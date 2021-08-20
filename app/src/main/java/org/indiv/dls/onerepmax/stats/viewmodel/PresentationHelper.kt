package org.indiv.dls.onerepmax.stats.viewmodel

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
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
        val currentYear = LocalDate.now().year

        val dataPoints = sortedResults.mapIndexed { index, result ->
            DataPoint(
                xAxisLabel = formatDateLabel(result.date, currentYear),
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

    private fun formatDateLabel(date: LocalDate, currentYear: Int): String {
        // Only include the year if a past year. User will assume year-less dates are the current year.
        val yearSuffix = if (date.year == currentYear) "" else " ${date.year}"
        return monthDayFormatter.format(date) + yearSuffix
    }
}
