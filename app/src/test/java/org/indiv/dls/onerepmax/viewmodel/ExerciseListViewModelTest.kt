package org.indiv.dls.onerepmax.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.indiv.dls.onerepmax.data.ExerciseRepository
import org.indiv.dls.onerepmax.data.ExerciseSummary
import org.indiv.dls.onerepmax.uicomponent.ExerciseSummaryView
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ExerciseListViewModelTest {

    companion object {
        private const val exerciseId = "123"
        private const val exerciseName = "Bench Press"
        private const val oneRepMax = 250u
        private val exerciseSummaries = listOf(ExerciseSummary(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            oneRepMaxPersonalRecord = oneRepMax
        ))
        private val exerciseSummaryPresentations = listOf(ExerciseSummaryView.Presentation(
            exerciseId = exerciseId,
            name = exerciseName,
            personalRecord = oneRepMax.toString()
        ))
    }

    // Needed for testing LiveData
    // (see https://medium.com/swlh/unit-testing-with-kotlin-coroutines-the-android-way-19289838d257)
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @MockK lateinit var exerciseRepository: ExerciseRepository
    @MockK lateinit var presentationHelper: PresentationHelper

    lateinit var exerciseListViewModel: ExerciseListViewModel

    private var observedExerciseSummaryPresentations: List<ExerciseSummaryView.Presentation>? = null

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testCoroutineDispatcher)

        coEvery { exerciseRepository.getExerciseSummaries() } returns exerciseSummaries
        every { presentationHelper.getExercises(exerciseSummaries) } returns exerciseSummaryPresentations

        // Construct view model after mocking its dependencies because it performs work in its init block
        exerciseListViewModel = ExerciseListViewModel(exerciseRepository, presentationHelper)

        exerciseListViewModel.exerciseListLiveData.observeForever { observedExerciseSummaryPresentations = it }
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun fetchExerciseListData() = runBlocking {
        // verify calls made from the view model init block upon construction
        assertEquals(exerciseSummaryPresentations, observedExerciseSummaryPresentations)
        coVerify { exerciseRepository.getExerciseSummaries() }
        verify { presentationHelper.getExercises(exerciseSummaries) }
    }
}