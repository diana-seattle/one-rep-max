package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.indiv.dls.onerepmax.data.ExerciseRepository
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exerciseRepository: ExerciseRepository,
    private val presentationHelper: PresentationHelper
) : ViewModel() {

    private val _exerciseDetailLiveData = MutableLiveData<ExerciseDetailPresentation>()
    val exerciseDetailLiveData: LiveData<ExerciseDetailPresentation> = _exerciseDetailLiveData

    fun fetchSingleExerciseData(name: String) {
        // This creates a coroutine on the main thread. The file reader and calculator are "main-safe" in that
        // they will switch themselves to the appropriate thread.
        viewModelScope.launch {
            exerciseRepository.getSingleExerciseData(name)?.let {
                _exerciseDetailLiveData.value = presentationHelper.getExerciseDetail(it)
            }
        }
    }
}
