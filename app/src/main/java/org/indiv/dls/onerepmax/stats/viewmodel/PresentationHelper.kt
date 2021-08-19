package org.indiv.dls.onerepmax.stats.viewmodel

import org.indiv.dls.onerepmax.data.ExerciseWithStats
import javax.inject.Inject

class PresentationHelper @Inject constructor() {

    fun getExercises(exerciseData: List<ExerciseWithStats>): List<ExercisePresentation> {
        return exerciseData.map { ExercisePresentation(it.exerciseName, it.oneRepMaxPersonalRecord.toString()) }
    }

    fun getExerciseDetail(exerciseWithStats: ExerciseWithStats): ExerciseDetailPresentation {
        return ExerciseDetailPresentation(
            exercise = ExercisePresentation(exerciseWithStats.exerciseName, exerciseWithStats.oneRepMaxPersonalRecord.toString()),
            dataPoints = exerciseWithStats.singleDayResults.map {
                DataPoint(date = it.date, oneRepMax = it.oneRepMax)
            }
        )
    }
}
