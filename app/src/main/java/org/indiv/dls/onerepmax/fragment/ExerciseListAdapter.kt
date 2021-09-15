package org.indiv.dls.onerepmax.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.indiv.dls.onerepmax.databinding.ViewholderExerciseBinding
import org.indiv.dls.onerepmax.uicomponent.ExerciseSummaryView
import javax.inject.Inject

class ExerciseListAdapter @Inject constructor()
    : ListAdapter<ExerciseSummaryView.Presentation, ExerciseViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object: DiffUtil.ItemCallback<ExerciseSummaryView.Presentation>() {
            override fun areItemsTheSame(oldItem: ExerciseSummaryView.Presentation, newItem: ExerciseSummaryView.Presentation): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ExerciseSummaryView.Presentation, newItem: ExerciseSummaryView.Presentation): Boolean {
                return oldItem == newItem
            }
        }
    }

    var itemClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ViewholderExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        with(getItem(position)) {
            holder.binding.root.bindPresentation(this)
            holder.binding.root.setOnClickListener {
                itemClickListener?.invoke(name)
            }
        }
    }
}
