package org.indiv.dls.onerepmax.stats.viewmodel

import org.indiv.dls.onerepmax.data.ExerciseWithStats

class PresentationHelper {

    fun getExercises(exerciseData: List<ExerciseWithStats>): List<Exercise> {
        return exerciseData.map { Exercise(it.exerciseName, it.oneRepMaxPersonalRecord.toString()) }
    }

    fun getExerciseDetail(exerciseWithStats: ExerciseWithStats): ExerciseDetail {
        return ExerciseDetail(
            exercise = Exercise(exerciseWithStats.exerciseName, exerciseWithStats.oneRepMaxPersonalRecord.toString()),
            dataPoints = exerciseWithStats.singleDayResults.map {
                DataPoint(date = it.date, oneRepMax = it.oneRepMax)
            }
        )
    }
}
