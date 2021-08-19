package org.indiv.dls.onerepmax.stats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.indiv.dls.onerepmax.databinding.FragmentExerciseDetailBinding
import org.indiv.dls.onerepmax.stats.viewmodel.ExercisesViewModel

@AndroidEntryPoint
class ExerciseDetailFragment: Fragment() {

    private val exercisesViewModel: ExercisesViewModel by activityViewModels()

    private var _binding: FragmentExerciseDetailBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel.exerciseDetailLiveData.observe(viewLifecycleOwner) {
            binding.exerciseSummary.exerciseName.text = it.exercise.name
            binding.exerciseSummary.onerepmaxPersonalRecord.text = it.exercise.personalRecord
        }
        exercisesViewModel.errorResultLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}