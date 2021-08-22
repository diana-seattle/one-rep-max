package org.indiv.dls.onerepmax.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.indiv.dls.onerepmax.databinding.FragmentExerciseDetailBinding
import org.indiv.dls.onerepmax.viewmodel.ExercisesViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.viewmodel.DataPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class ExerciseDetailFragment : Fragment() {

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

        exercisesViewModel.exerciseDetailLiveData.observe(viewLifecycleOwner) { presentation ->
            with(binding.exerciseSummary) {
                exerciseName.text = presentation.exerciseSummary.name
                onerepmaxPersonalRecord.text = presentation.exerciseSummary.personalRecord
            }

            val entries = presentation.dataPoints.map { Entry(it.xAxisValue, it.yAxisValue) }
            val lineDataSet = createLineDataSet(entries)

            configureChart(presentation.dataPoints, lineDataSet)
            binding.chart.invalidate() // refresh
        }

        exercisesViewModel.errorResultLiveData.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureChart(
        dataPoints: List<DataPoint>,
        lineDataSet: LineDataSet
    ) {
        val textColor = getThemeColor(android.R.attr.textColor)

        with(binding.chart) {
            axisRight.isEnabled = false
            legend.isEnabled = false
            description = null
            with(xAxis) {
                setTextColor(textColor)
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return dataPoints[value.toInt()].xAxisLabel
                    }
                }
            }

            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return resources.getString(R.string.yaxis_label, value.roundToInt())
                }
            }
            axisLeft.textColor = textColor

            data = LineData(lineDataSet)
        }
    }

    private fun createLineDataSet(entries: List<Entry>): LineDataSet {
        val lineColor = getThemeColor(R.attr.colorPrimary)
        return LineDataSet(entries, "").apply {
            setDrawValues(false)
            setDrawCircleHole(false)
            color = lineColor
            setCircleColor(lineColor)
        }
    }

    private fun getThemeColor(@AttrRes colorAttributeResId: Int): Int {
        return MaterialColors.getColor(binding.root, colorAttributeResId)
    }
}