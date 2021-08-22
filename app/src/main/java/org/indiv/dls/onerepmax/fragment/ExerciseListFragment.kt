package org.indiv.dls.onerepmax.fragment

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
import org.indiv.dls.onerepmax.databinding.FragmentExerciseListBinding
import org.indiv.dls.onerepmax.viewmodel.MainActivityViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ExerciseListFragment : Fragment() {

    @Inject lateinit var exerciseListAdapter: ExerciseListAdapter

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentExerciseListBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExerciseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewAdapter()
        setupErrorHandling()
        mainActivityViewModel.fetchExerciseListData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViewAdapter() {
        binding.exerciseRecyclerView.adapter = exerciseListAdapter
        mainActivityViewModel.exerciseListLiveData.observe(viewLifecycleOwner) {
            exerciseListAdapter.items = it
            exerciseListAdapter.notifyDataSetChanged()
        }
        setupItemClickListener()
    }

    private fun setupItemClickListener() {
        exerciseListAdapter.itemClickListener = { exerciseName ->
            val args = Bundle().apply { putString(resources.getString(R.string.key_exercise_name), exerciseName) }
            findNavController().navigate(R.id.action_ExerciseListFragment_to_ExerciseDetailFragment, args)
        }
    }

    private fun setupErrorHandling() {
        mainActivityViewModel.errorResultLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE).apply {
                setAction(getString(R.string.button_text_ok)) { dismiss() }
                show()
            }
        }
    }
}

