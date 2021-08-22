package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.data.ExerciseRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    fun includeDarkModeMenuItem(): Boolean {
        // Android 9 (P) and below do not have dark mode in the system settings, so provide a menu for it
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
    }

    fun setDarkMode(dark: Boolean) {
        val mode = if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getDarkModeActionTitleForExistingState(dark: Boolean): String {
        val resId = if (dark) R.string.action_to_light_mode else R.string.action_to_dark_mode
        return context.getString(resId)
    }

    fun isDarkModeInSettings(): Boolean {
        return exerciseRepository.isDarkModeInSettings()
    }

    fun persistDarkMode(dark: Boolean) {
        exerciseRepository.persistDarkMode(dark)
    }
}
