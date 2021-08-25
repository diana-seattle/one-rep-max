package org.indiv.dls.onerepmax.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.indiv.dls.onerepmax.data.ExerciseRepository
import org.indiv.dls.onerepmax.data.ExerciseSummary
import org.indiv.dls.onerepmax.data.ExerciseWithStats
import org.indiv.dls.onerepmax.data.SingleDayResult
import org.indiv.dls.onerepmax.uicomponent.ChartView
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ExerciseDetailViewModelTest {

    companion object {
        private const val exerciseName = "Bench Press"
        private const val oneRepMax = 250u
        private val exerciseWithStats = ExerciseWithStats(
            exerciseSummary = ExerciseSummary(exerciseName, oneRepMax),
            singleDayResults = listOf(SingleDayResult(LocalDate.now(), oneRepMax))
        )
        private val exerciseDetailPresentation = ExerciseDetailPresentation(
            exercise = ExercisePresentation(name = exerciseName, personalRecord = oneRepMax.toString()),
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
    @MockK lateinit var exerciseRepository: ExerciseRepository
    @MockK lateinit var presentationHelper: PresentationHelper

    @InjectMockKs lateinit var exerciseDetailViewModel: ExerciseDetailViewModel

    private var observedExerciseDetailPresentation: ExerciseDetailPresentation? = null

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testCoroutineDispatcher)

        coEvery { exerciseRepository.getSingleExerciseDetail(exerciseName) } returns exerciseWithStats
        every { presentationHelper.getExerciseDetail(exerciseWithStats) } returns exerciseDetailPresentation

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
        exerciseDetailViewModel.fetchSingleExerciseData(exerciseName)
        assertEquals(exerciseDetailPresentation, observedExerciseDetailPresentation)
        coVerify { exerciseRepository.getSingleExerciseDetail(exerciseName) }
        verify { presentationHelper.getExerciseDetail(exerciseWithStats) }
    }
}