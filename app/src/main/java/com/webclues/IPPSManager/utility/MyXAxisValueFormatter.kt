package com.webclues.IPPSManager.utility

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyXAxisValueFormatter() : IndexAxisValueFormatter() {

    override fun getFormattedValue(value: Float): String? {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user



        val dateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM")
        val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
        var time : String = ""
     /*   if(list.size > 0){
            for (i in list.indices){*/
                val date = value.toLong()

                // Show time in local version
                val timeMilliseconds = Date(date)

             /*   if(i==0){
                    time =dateFormat.format(timeMilliseconds)
                }else{*/
                    time =timeFormat.format(timeMilliseconds)
//                }
//            }
//        }

        Log.e("X-Axis Formatter-->",time)
        return time
    }
}