package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exerciseRepository: ExerciseRepository,
    private val presentationHelper: PresentationHelper
) : ViewModel() {

    private val _exerciseListLiveData = MutableLiveData<List<ExercisePresentation>>()
    val exerciseListLiveData: LiveData<List<ExercisePresentation>> = _exerciseListLiveData

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

    fun includeDarkModeMenuItem(): Boolean {
        // Android 9 (P) and below do not have dark mode in the system settings, so provide a menu for it
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
    }

    fun setDarkMode(dark: Boolean) {
        val mode = if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getDarkModeActionTitleForState(dark: Boolean): String {
        val resId = if (dark) R.string.action_light_mode else R.string.action_dark_mode
        return context.getString(resId)
    }

    fun isDarkModeInSettings(): Boolean {
        return exerciseRepository.isDarkModeInSettings()
    }

    fun persistDarkMode(dark: Boolean) {
        exerciseRepository.persistDarkMode(dark)
    }
}
