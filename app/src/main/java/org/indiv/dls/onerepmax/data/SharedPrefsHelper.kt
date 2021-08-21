package org.indiv.dls.onerepmax.data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsHelper @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private const val PREFS_FILENAME = "one-rep-max-prefs"
        private const val PREFS_KEY_DARK_MODE = "dark"
    }

    fun isDarkMode(): Boolean {
        return getSharedPreferences().getBoolean(PREFS_KEY_DARK_MODE, false)
    }

    fun persistDarkMode(dark: Boolean) {
        getSharedPreferences().edit().putBoolean(PREFS_KEY_DARK_MODE, dark).apply()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(PREFS_FILENAME, AppCompatActivity.MODE_PRIVATE)
    }
}
