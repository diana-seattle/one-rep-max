package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.data.ExerciseRepository
import org.indiv.dls.onerepmax.data.StatsRecord
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DataInputViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {


    //todo: use sealed classes for result, wait on success before dismissing dialog

    private val _inputLiveData = MutableLiveData<InputData>()
    val inputLiveData: LiveData<InputData> = _inputLiveData

    private val _actionCompletionLiveData = MutableLiveData<Boolean>()
    val actionCompletionLiveData: LiveData<Boolean> = _actionCompletionLiveData

    fun onInputChange(data: InputData) {
        _inputLiveData.value = if (data.errorMessage != null && validate(data)) {
            data.copy(errorMessage = null)
        } else data
    }

    fun saveData() {
        val requiredFieldsErrorMsg = context.resources.getString(R.string.error_required_fields)
        val currentValue = inputLiveData.value ?: InputData()
        if (!validate(currentValue)) {
            _inputLiveData.value = currentValue.copy(errorMessage = requiredFieldsErrorMsg)
        } else {
            val statsRecord = StatsRecord(
                dateOfWorkout = currentValue.dateOfWorkout!!,
                exerciseName = currentValue.exerciseName,
                sets = currentValue.sets!!,
                reps = currentValue.reps!!,
                weight = currentValue.weight!!
            )

            viewModelScope.launch {
                try {
                    exerciseRepository.addStatsRecord(statsRecord)
                    _actionCompletionLiveData.value = true
                } catch (e: Exception) {
                    _inputLiveData.value = currentValue.copy(errorMessage =
                        context.resources.getString(R.string.error_unable_to_save_data))
                }
            }
        }
    }

    fun resetToFile() {
        val currentValue = inputLiveData.value ?: InputData()
        viewModelScope.launch {
            try {
                exerciseRepository.resetToFile()
                _actionCompletionLiveData.value = true
            } catch (e: Exception) {
                _inputLiveData.value = currentValue.copy(errorMessage =
                    context.resources.getString(R.string.error_unable_to_reset))
            }
        }
    }

    private fun validate(inputData: InputData): Boolean {
        return with(inputData) {
            exerciseName.isNotEmpty()
                    && dateOfWorkout != null
                    && sets != null && sets!! > 0u
                    && reps != null && reps!! > 0u
                    && weight != null && weight!! > 0u
        }
    }
}

data class InputData(
    var dateOfWorkout: LocalDate? = LocalDate.now(),
    var exerciseName: String = "",
    var sets: UInt? = 1u,
    var reps: UInt? = null,
    var weight: UInt? = null,
    var errorMessage: String? = null
)

