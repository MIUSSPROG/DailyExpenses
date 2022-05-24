package com.example.dailyexpenses.ui.main.tabs.diagram

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.data.DaoResponse
import com.example.dailyexpenses.data.DiagramData
import com.example.dailyexpenses.data.HistogramData
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.UiState
import com.example.dailyexpenses.utils.prefs
import kotlinx.coroutines.launch
import java.lang.Exception

class DiagramViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _dataForHistogram = MutableLiveData<UiState<List<HistogramData>>>()
    val dataForHistogram: LiveData<UiState<List<HistogramData>>> = _dataForHistogram

    private val _dataForDiagram = MutableLiveData<List<DiagramData>>()
    val dataForDiagram: LiveData<List<DiagramData>> = _dataForDiagram


    fun getDataForHistogram(fromDate: Long, toDate: Long){
        viewModelScope.launch {
            val response = expensesRepository.getDaoSource().getAllItemsInRange(prefs.id, fromDate, toDate)
            when(response){
                is DaoResponse.Success<*> -> {
                    _dataForHistogram.postValue(UiState.Success(response.data as List<HistogramData>))
                }
                is DaoResponse.Error -> {
                    Log.d("Error", response.exception.message.toString())
                    _dataForHistogram.postValue(UiState.Error(Exception("Ошибка получения данных")))
                }
            }
//            expensesRepository.getItemToBuyDao().getAllItemsInRange(prefs.id, fromDate, toDate).collect {
//                _dataForHistogram.postValue(it)
//            }
        }
    }

    fun getDataForDiagram(fromDate: Long, toDate: Long){
        viewModelScope.launch {
            val response = expensesRepository.getDaoSource().getAllItemsByCategory(prefs.id, fromDate, toDate)
            when(response){
                is DaoResponse.Success
            }
        }
    }

}