package org.indiv.dls.onerepmax.uicomponent

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

import android.view.LayoutInflater
import org.indiv.dls.onerepmax.databinding.ViewExerciseSummaryBinding


class ExerciseSummaryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    data class Presentation(val exerciseId: String, val name: String, val personalRecord: String)

    var binding: ViewExerciseSummaryBinding =
        ViewExerciseSummaryBinding.inflate(LayoutInflater.from(context), this)

    fun bindPresentation(presentation: Presentation) {
        binding.exerciseName.text = presentation.name
        binding.onerepmaxPersonalRecord.text = presentation.personalRecord
    }
}
