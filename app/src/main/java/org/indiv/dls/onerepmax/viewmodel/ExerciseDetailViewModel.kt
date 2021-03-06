package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.data.ExerciseRepository
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val exerciseRepository: ExerciseRepository,
    private val presentationHelper: PresentationHelper
) : ViewModel() {

    private val _exerciseDetailLiveData = MutableLiveData<ExerciseDetailPresentation>()
    val exerciseDetailLiveData: LiveData<ExerciseDetailPresentation> = _exerciseDetailLiveData

    private val _errorResultLiveData = MutableLiveData<String>()
    val errorResultLiveData: LiveData<String> = _errorResultLiveData

    val dataChangeLiveData: LiveData<Boolean>
        get() = exerciseRepository.dataChangeLiveData

    val exerciseId: String = savedStateHandle.get<String>(context.resources.getString(R.string.key_exercise_id))!!

    fun fetchSingleExerciseData() {
        // This creates a coroutine on the main thread. The file reader and calculator are "main-safe"
        // in that they will switch themselves to the appropriate thread.
        viewModelScope.launch {
            try {
                val detail = exerciseRepository.getSingleExerciseDetail(exerciseId)
                if (detail != null) {
                    _exerciseDetailLiveData.value = presentationHelper.getExerciseDetail(detail)
                } else {
                    _errorResultLiveData.value = context.resources.getString(R.string.error_not_found)
                }
            } catch (e: Exception) {
                _errorResultLiveData.value = e.message
            }
        }
    }
}
