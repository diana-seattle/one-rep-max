package org.indiv.dls.onerepmax.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.indiv.dls.onerepmax.databinding.ViewholderExerciseBinding
import org.indiv.dls.onerepmax.viewmodel.ExercisePresentation
import javax.inject.Inject

class ExerciseListAdapter @Inject constructor() : RecyclerView.Adapter<ExerciseViewHolder>() {

    var items: List<ExercisePresentation> = emptyList()
    var itemClickListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ViewholderExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        with(items[position]) {
            holder.binding.exerciseName.text = name
            holder.binding.onerepmaxPersonalRecord.text = personalRecord
            holder.binding.root.setOnClickListener {
                itemClickListener?.invoke(items[position].name)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
