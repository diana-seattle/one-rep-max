package org.indiv.dls.onerepmax.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.databinding.ActivityMainBinding
import org.indiv.dls.onerepmax.viewmodel.MainActivityViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        mainActivityViewModel.fetchExerciseListData()
        mainActivityViewModel.errorResultLiveData.observe(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE).apply {
                setAction(getString(R.string.button_text_ok)) { dismiss() }
                show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (mainActivityViewModel.includeDarkModeMenuItem()) {
            menuInflater.inflate(R.menu.menu_main, menu)
            val isDarkModeInSettings = mainActivityViewModel.isDarkModeInSettings()
            mainActivityViewModel.setDarkMode(isDarkModeInSettings)
            setDarkModeMenuText(menu.getItem(0), isDarkModeInSettings)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_dark_mode_toggle -> {
                val newStateIsDark = !mainActivityViewModel.isDarkModeInSettings()
                mainActivityViewModel.setDarkMode(newStateIsDark)
                mainActivityViewModel.persistDarkMode(newStateIsDark)
                setDarkModeMenuText(item, newStateIsDark)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setDarkModeMenuText(item: MenuItem, currentlyDark: Boolean) {
        item.title = mainActivityViewModel.getDarkModeActionTitleForState(currentlyDark)
    }
}
