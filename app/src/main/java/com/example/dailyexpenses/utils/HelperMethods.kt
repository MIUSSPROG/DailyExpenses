package com.example.dailyexpenses.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
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

        fun getRealPathFromURI(context: Context, imageURI: Uri): String?{
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(imageURI, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
                cursor!!.moveToNext()
                val fileName = cursor.getString(0)
                val path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                if (!TextUtils.isEmpty(path)) {
                    return path
                }
            } finally {
                cursor?.close()
            }
            return null
        }
    }
}

sealed class UiState<T>(
    data: T? = null,
    exception: Exception? = null
){
    data class Success<T>(val data: T? = null): UiState<T>(data, null)
    data class Failure<T>(val data: T? = null): UiState<T>(data, null)
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