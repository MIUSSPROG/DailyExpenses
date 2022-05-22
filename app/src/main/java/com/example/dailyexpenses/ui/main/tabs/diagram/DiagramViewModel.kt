package com.example.dailyexpenses.ui.main.tabs.diagram

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.data.DiagramData
import com.example.dailyexpenses.data.HistogramData
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.prefs
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DiagramViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _dataForHistogram = MutableLiveData<List<HistogramData>>()
    val dataForHistogram: LiveData<List<HistogramData>> = _dataForHistogram

    private val _dataForDiagram = MutableLiveData<List<DiagramData>>()
    val dataForDiagram: LiveData<List<DiagramData>> = _dataForDiagram


    fun getDataForHistogram(fromDate: Long, toDate: Long){
        viewModelScope.launch {
            expensesRepository.getItemToBuyDao().getAllItemsInRange(prefs.id, fromDate, toDate).collect {
                _dataForHistogram.postValue(it)
            }
        }
    }

    fun getDataForDiagram(fromDate: Long, toDate: Long){
        viewModelScope.launch {
            expensesRepository.getItemToBuyDao().getAllItemsByCategory(prefs.id, fromDate, toDate).collect {
                _dataForDiagram.postValue(it)
            }
        }
    }
}