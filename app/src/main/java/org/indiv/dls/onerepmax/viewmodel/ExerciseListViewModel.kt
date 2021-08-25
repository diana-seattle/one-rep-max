package org.indiv.dls.onerepmax.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.indiv.dls.onerepmax.data.ExerciseRepository
import org.indiv.dls.onerepmax.uicomponent.ExerciseSummaryView
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val presentationHelper: PresentationHelper
) : ViewModel() {

    private val _exerciseListLiveData = MutableLiveData<List<ExerciseSummaryView.Presentation>>()
    val exerciseListLiveData: LiveData<List<ExerciseSummaryView.Presentation>> = _exerciseListLiveData

    private val _errorResultLiveData = MutableLiveData<String>()
    val errorResultLiveData: LiveData<String> = _errorResultLiveData

    fun fetchExerciseListData() {
        // This creates a coroutine on the main thread. The file reader and calculator are "main-safe" in that
        // they will switch themselves to the appropriate thread.
        viewModelScope.launch {
            try {
                val exerciseSummaries = exerciseRepository.getExerciseSummaries()
                _exerciseListLiveData.value = presentationHelper.getExercises(exerciseSummaries)
            } catch (e: Exception) {
                _errorResultLiveData.value = e.message
            }
        }
    }
}
