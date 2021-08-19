package org.indiv.dls.onerepmax.stats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.indiv.dls.onerepmax.data.ExerciseWithStats
import org.indiv.dls.onerepmax.data.StatsCalculator
import org.indiv.dls.onerepmax.data.StatsFileReader
import java.lang.Exception

class ExercisesViewModel(
    val statsFileReader: StatsFileReader,
    val statsCalculator: StatsCalculator,
    val presentationHelper: PresentationHelper
) : ViewModel() {

    private val _exerciseListLiveData = MutableLiveData<List<ExercisePresentation>>()
    val exerciseListLiveData: LiveData<List<ExercisePresentation>> = _exerciseListLiveData

    private val _exerciseDetailLiveData = MutableLiveData<ExerciseDetailPresentation>()
    val exerciseDetailLiveData: LiveData<ExerciseDetailPresentation> = _exerciseDetailLiveData

    private val _errorResultLiveData = MutableLiveData<String>()
    val errorResultLiveData: LiveData<String> = _errorResultLiveData

    // Master copy of all the data.
    // Todo: If time, read results from the data file into a db at app load time, then have this viewmodel load only
    //   the results needed from the db for the particular page.
    private var exerciseData: List<ExerciseWithStats> = emptyList()
    private var exerciseDataMap: Map<String, ExerciseWithStats> = emptyMap()

    fun fetchExerciseListData() {
        // This creates a coroutine on the main thread. The file reader and calculator are "main-safe" in that they will
        // switch themselves to the appropriate thread.
        viewModelScope.launch {
            try {
                // TODO: if time, read results from file into db at app load time, then have this viewmodel load only
                //  the results needed from db.
                exerciseData = statsCalculator.calculate(statsFileReader.readFile())
                exerciseDataMap = exerciseData.map { it.exerciseName to it }.toMap()

                _exerciseListLiveData.value = presentationHelper.getExercises(exerciseData)

            } catch (e: Exception) {
                _errorResultLiveData.value = e.message
            }
        }
    }

    fun emitSingleExerciseData(name: String) {
        // This creates a coroutine on the main thread. The file reader and calculator are "main-safe" in that they will
        // switch themselves to the appropriate thread.
        viewModelScope.launch {
            if (exerciseData.isEmpty()) {
                fetchExerciseListData()
            }

            exerciseDataMap[name]?.let {
                _exerciseDetailLiveData.value = presentationHelper.getExerciseDetail(it)
            }
        }
    }
}
