package com.example.dailyexpenses.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat

class HelperMethods {
    companion object{
        fun convertMillisToDate(millis: Long): String{
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            return sdf.format(millis)
        }

        fun convertDateToMillis(date: String): Long{
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            return sdf.parse(date).time
        }

        fun convertMillisToDateMills(millis: Long): Long{
            val tmp = convertMillisToDate(millis)
            return convertDateToMillis(tmp)
        }
    }
}

class XAxisDateFormatter: ValueFormatter(){
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val tmp =  HelperMethods.convertMillisToDate(HelperMethods.convertMillisToDateMills(value.toLong())).split('/')[0]
        return tmp
    }
}