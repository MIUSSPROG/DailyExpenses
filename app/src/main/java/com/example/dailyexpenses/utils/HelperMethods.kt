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

sealed class UiState<T>(
    data: T? = null,
    exception: Exception? = null
){
    data class Success<T>(val data: T? = null): UiState<T>(data, null)
    data class Error<T>(val exception: Exception): UiState<T>(null, exception)
}

//class PieAxisCategoryNameFormatter: ValueFormatter(){
//    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//        return
//    }
//}

class XAxisDateFormatter: ValueFormatter(){
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val tmp =  HelperMethods.convertMillisToDate(HelperMethods.convertMillisToDateMills(value.toLong())).split('/')[0]
        return tmp
    }
}