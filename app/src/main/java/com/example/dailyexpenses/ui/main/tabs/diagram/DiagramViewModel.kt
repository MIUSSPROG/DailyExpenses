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

    private val _dataForDiagram = MutableLiveData<UiState<List<DiagramData>>>()
    val dataForDiagram: LiveData<UiState<List<DiagramData>>> = _dataForDiagram


    fun getDataForHistogram(fromDate: Long, toDate: Long){
        viewModelScope.launch {
            when(val response = expensesRepository.getDaoSource().getAllItemsInRange(prefs.id, fromDate, toDate)){
                is DaoResponse.Success -> {
                    _dataForHistogram.postValue(UiState.Success(response.data))
                }
                is DaoResponse.Error -> {
                    Log.d("Error", response.exception.message.toString())
                    _dataForHistogram.postValue(UiState.Error(Exception("Ошибка получения данных")))
                }
            }
        }
    }

    fun getDataForDiagram(fromDate: Long, toDate: Long){
        viewModelScope.launch {
            when(val response = expensesRepository.getDaoSource().getAllItemsByCategory(prefs.id, fromDate, toDate)){
                is DaoResponse.Success -> {
                    _dataForDiagram.postValue(UiState.Success(response.data))
                }
                is DaoResponse.Error -> {
                    Log.d("Error", response.exception.message.toString())
                    _dataForDiagram.postValue(UiState.Error(Exception("Ошибка получения данных")))
                }
            }
        }
    }
}