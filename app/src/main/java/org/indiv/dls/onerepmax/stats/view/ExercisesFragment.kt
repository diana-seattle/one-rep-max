package org.indiv.dls.onerepmax.stats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.databinding.FragmentExerciseBinding
import org.indiv.dls.onerepmax.stats.viewmodel.ExercisesViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ExercisesFragment : Fragment() {

    @Inject lateinit var exerciseListAdapter: ExerciseListAdapter

    private val exercisesViewModel: ExercisesViewModel by activityViewModels()

    private var _binding: FragmentExerciseBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseListAdapter.itemClickListener = { exerciseName ->
            exercisesViewModel.selectSingleExerciseData(exerciseName)
            val args = Bundle().apply { putString(resources.getString(R.string.key_exercise_name), exerciseName) }
            findNavController().navigate(R.id.action_ExerciseFragment_to_ExerciseDetailFragment, args)
        }

        binding.exerciseRecyclerView.adapter = exerciseListAdapter
        exercisesViewModel.exerciseListLiveData.observe(viewLifecycleOwner) {
            exerciseListAdapter.items = it
            exerciseListAdapter.notifyDataSetChanged()
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

