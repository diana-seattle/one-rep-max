package org.indiv.dls.onerepmax.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.indiv.dls.onerepmax.databinding.FragmentExerciseDetailBinding
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.viewmodel.ExerciseDetailViewModel


@AndroidEntryPoint
class ExerciseDetailFragment : Fragment() {

    private val exerciseDetailViewModel: ExerciseDetailViewModel by viewModels()

    private var _binding: FragmentExerciseDetailBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exerciseNameArgKey = resources.getString(R.string.key_exercise_name)
        exerciseDetailViewModel.fetchSingleExerciseData(arguments?.getString(exerciseNameArgKey)!!)

        exerciseDetailViewModel.exerciseDetailLiveData.observe(viewLifecycleOwner) { presentation ->
            binding.exerciseSummary.bindPresentation(presentation.exerciseSummary)
            binding.chart.setData(presentation.dataPoints)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
