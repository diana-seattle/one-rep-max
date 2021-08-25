package org.indiv.dls.onerepmax.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.indiv.dls.onerepmax.databinding.ViewholderExerciseBinding
import org.indiv.dls.onerepmax.uicomponent.ExerciseSummaryView
import javax.inject.Inject

class ExerciseListAdapter @Inject constructor() : RecyclerView.Adapter<ExerciseViewHolder>() {

    var items: List<ExerciseSummaryView.Presentation> = emptyList()
    var itemClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ViewholderExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        with(items[position]) {
            holder.binding.root.bindPresentation(this)
            holder.binding.root.setOnClickListener {
                itemClickListener?.invoke(name)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
