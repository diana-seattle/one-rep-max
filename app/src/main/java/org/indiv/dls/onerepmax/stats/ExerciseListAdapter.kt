package org.indiv.dls.onerepmax.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.indiv.dls.onerepmax.databinding.ViewholderExerciseBinding

class ExerciseListAdapter : RecyclerView.Adapter<ExerciseViewHolder>() {

    var items: List<ExercisePresentation> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ViewholderExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        with(items[position]) {
            holder.binding.exerciseName.text = name
            holder.binding.onerepmaxPersonalRecord.text = oneRepMaxRecord
            holder.binding.root.setOnClickListener {
                //todo
                Snackbar.make(it, "open fragment for: $name", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
