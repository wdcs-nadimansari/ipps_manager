package com.webclues.IPPSManager.utility

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MyYAxisValueFormatter(val type: String) : IndexAxisValueFormatter() {
    private var suffix: String? = null
    override fun getFormattedValue(value: Float): String? {
        when (type){
            "Current" -> {
               suffix="A"
            }
            "Voltage" -> {
                suffix="V"
            }
            "Power" -> {
                suffix="W"
            }
            "Temperature" -> {
                suffix="F"
            }
        }

        return ""
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return super.getAxisLabel(value, axis)
    }
}