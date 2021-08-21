package org.indiv.dls.onerepmax.stats.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.indiv.dls.onerepmax.databinding.FragmentExerciseDetailBinding
import org.indiv.dls.onerepmax.stats.viewmodel.ExercisesViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import org.indiv.dls.onerepmax.R
import org.indiv.dls.onerepmax.stats.viewmodel.DataPoint
import org.indiv.dls.onerepmax.stats.viewmodel.ExerciseDetailPresentation
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

    private fun configureChart(
        dataPoints: List<DataPoint>,
        lineDataSet: LineDataSet
    ) {
        with(binding.chart) {
            axisRight.isEnabled = false
            getLegend().isEnabled = false
            description = null
            xAxis.labelRotationAngle = 45f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return dataPoints[value.toInt()].xAxisLabel
                }
            }
            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return resources.getString(R.string.yaxis_label, value.roundToInt())
                }
            }
            data = LineData(lineDataSet)
        }
    }

    private fun createLineDataSet(entries: List<Entry>): LineDataSet {
        val lineColor = MaterialColors.getColor(binding.root, R.attr.colorPrimary)
        return LineDataSet(entries, "").apply {
            setDrawValues(false)
            setDrawCircleHole(false)
            color = lineColor
            setCircleColor(lineColor)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}