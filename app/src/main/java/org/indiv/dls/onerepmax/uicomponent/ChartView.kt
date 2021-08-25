package org.indiv.dls.onerepmax.uicomponent

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.color.MaterialColors
import org.indiv.dls.onerepmax.R
import kotlin.math.roundToInt

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LineChart(context, attrs, defStyleAttr) {

    data class DataPoint(
        val xAxisLabel: String,
        val xAxisValue: Float,
        val yAxisValue: Float
    )

    init {
        axisRight.isEnabled = false
        legend.isEnabled = false
        description = null
        val textColor = getThemeColor(android.R.attr.textColor)
        with(xAxis) {
            setTextColor(textColor)
            position = XAxis.XAxisPosition.BOTTOM
        }
        axisLeft.textColor = textColor
    }

    fun setData(dataPoints: List<DataPoint>) {
        configureXAxis(xAxis, dataPoints)
        configureYAxis(axisLeft)

        val entries = dataPoints.map { Entry(it.xAxisValue, it.yAxisValue) }
        val lineDataSet = createLineDataSet(entries)
        data = LineData(lineDataSet)
        invalidate() // refresh chart
    }

    private fun configureXAxis(xAxis: XAxis, dataPoints: List<DataPoint>) {
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return dataPoints[value.toInt()].xAxisLabel
            }
        }
    }

    private fun configureYAxis(yAxis: YAxis) {
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return resources.getString(R.string.yaxis_label, value.roundToInt())
            }
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
        return MaterialColors.getColor(this, colorAttributeResId)
    }
}
