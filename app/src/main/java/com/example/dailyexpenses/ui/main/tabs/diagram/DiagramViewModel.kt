package com.example.dailyexpenses.ui.main.tabs.diagram

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.data.DiagramData
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DiagramViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    val dataForDiagram = MutableLiveData<List<DiagramData>>()

//    fun createChild(child: Child){
//        viewModelScope.launch {
//            expensesRepository.getRemoteDataSource().createChild(child)
//        }
//    }

    fun getDataForDiagram(fromDate: Long, toDate: Long){
        viewModelScope.launch {
            expensesRepository.getItemToBuyDao().getAllItemsInRange(fromDate, toDate).collect {
                dataForDiagram.postValue(it)
            }
        }
    }
}