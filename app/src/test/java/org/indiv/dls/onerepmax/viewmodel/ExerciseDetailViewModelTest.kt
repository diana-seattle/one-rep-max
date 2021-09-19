package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.data.ExerciseRepository
import org.indiv.dls.onerepmax.data.ExerciseSummary
import org.indiv.dls.onerepmax.data.ExerciseWithStats
import org.indiv.dls.onerepmax.data.SingleDayResult
import org.indiv.dls.onerepmax.uicomponent.ChartView
import org.indiv.dls.onerepmax.uicomponent.ExerciseSummaryView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ExerciseDetailViewModelTest {

    companion object {
        private const val exerciseId = "123"
        private const val exerciseName = "Bench Press"
        private const val exerciseIdKey = "myExerciseNameKey"
        private const val oneRepMax = 250u
        private val exerciseWithStats = ExerciseWithStats(
            exerciseSummary = ExerciseSummary(exerciseId, exerciseName, oneRepMax),
            singleDayResults = listOf(SingleDayResult(LocalDate.now(), oneRepMax))
        )
        private val exerciseDetailPresentation = ExerciseDetailPresentation(
            exerciseSummary = ExerciseSummaryView.Presentation(
                exerciseId = exerciseId,
                name = exerciseName,
                personalRecord = oneRepMax.toString()
            ),
            dataPoints = listOf(ChartView.DataPoint("Jul 3", 1f, 50f))
        )
    }

    // Needed for testing LiveData
    // (see https://medium.com/swlh/unit-testing-with-kotlin-coroutines-the-android-way-19289838d257)
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    // See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @MockK lateinit var context: Context
    @MockK lateinit var resources: Resources
    @MockK lateinit var savedStateHandle: SavedStateHandle
    @MockK lateinit var exerciseRepository: ExerciseRepository
    @MockK lateinit var presentationHelper: PresentationHelper

    lateinit var exerciseDetailViewModel: ExerciseDetailViewModel

    private var observedExerciseDetailPresentation: ExerciseDetailPresentation? = null

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testCoroutineDispatcher)

        coEvery { exerciseRepository.getSingleExerciseDetail(exerciseId) } returns exerciseWithStats
        every { presentationHelper.getExerciseDetail(exerciseWithStats) } returns exerciseDetailPresentation
        every { context.resources } returns resources
        every { resources.getString(R.string.key_exercise_id) } returns exerciseIdKey
        every { savedStateHandle.get<String>(exerciseIdKey) } returns exerciseId

        // Construct view model after mocking its dependencies because it performs work in its init block
        exerciseDetailViewModel = ExerciseDetailViewModel(context, savedStateHandle, exerciseRepository, presentationHelper)

        exerciseDetailViewModel.exerciseDetailLiveData.observeForever { observedExerciseDetailPresentation = it }
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun fetchSingleExerciseData() = runBlocking {
        exerciseDetailViewModel.fetchSingleExerciseData()

        verify { context.resources }
        verify { resources.getString(R.string.key_exercise_id) }
        verify { savedStateHandle.get<String>(exerciseIdKey) }
        coVerify { exerciseRepository.getSingleExerciseDetail(exerciseId) }
        verify { presentationHelper.getExerciseDetail(exerciseWithStats) }
        assertEquals(exerciseDetailPresentation, observedExerciseDetailPresentation)
    }
}