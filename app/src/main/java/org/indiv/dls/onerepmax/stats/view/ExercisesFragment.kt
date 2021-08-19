package org.indiv.dls.onerepmax.stats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.indiv.dls.onerepmax.databinding.FragmentExerciseBinding
import org.indiv.dls.onerepmax.stats.viewmodel.ExercisePresentation

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExerciseBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ExerciseListAdapter()
        adapter.items = listOf(
            ExercisePresentation("Ex1", "155"),
            ExercisePresentation("Ex2", "111"),
        )

        binding.exerciseList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

