package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.data.ExerciseRepository
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class MainActivityViewModelTest {
    companion object {
        private const val toDarkModeAction = "change to dark"
        private const val toLightModeAction = "change to light"
    }

    @MockK lateinit var context: Context
    @MockK lateinit var exerciseRepository: ExerciseRepository

    @InjectMockKs lateinit var mainActivityViewModel: MainActivityViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { context.getString(R.string.action_to_dark_mode) } returns toDarkModeAction
        every { context.getString(R.string.action_to_light_mode) } returns toLightModeAction
    }

    @Test
    fun getDarkModeActionTitleForExistingState_dark() {
        assertEquals(toLightModeAction, mainActivityViewModel.getDarkModeActionTitleForExistingState(dark = true))
    }

    @Test
    fun getDarkModeActionTitleForExistingState_light() {
        assertEquals(toDarkModeAction, mainActivityViewModel.getDarkModeActionTitleForExistingState(dark = false))
    }

    @Test
    fun isDarkModeInSettings_true() {
        every { exerciseRepository.isDarkModeInSettings() } returns true
        assertTrue(mainActivityViewModel.isDarkModeInSettings())
        verify { exerciseRepository.isDarkModeInSettings() }
    }

    @Test
    fun isDarkModeInSettings_false() {
        every { exerciseRepository.isDarkModeInSettings() } returns false
        assertFalse(mainActivityViewModel.isDarkModeInSettings())
        verify { exerciseRepository.isDarkModeInSettings() }
    }

    @Test
    fun persistDarkMode_true() {
        every { exerciseRepository.persistDarkMode(any()) } just Runs
        mainActivityViewModel.persistDarkMode(true)
        verify { exerciseRepository.persistDarkMode(true) }
    }

    @Test
    fun persistDarkMode_false() {
        every { exerciseRepository.persistDarkMode(any()) } just Runs
        mainActivityViewModel.persistDarkMode(false)
        verify { exerciseRepository.persistDarkMode(false) }
    }
}