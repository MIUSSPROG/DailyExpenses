package com.example.dailyexpenses.ui.main.tabs.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.ItemToBuyDao
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class DashboardViewModel @ViewModelInject constructor(
//    private val itemToBuyDao: ItemToBuyDao
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    val itemToBuyLiveData = MutableLiveData<List<ItemToBuy>>()

    fun getItemsToBuy(pickedDate: String){
        val selectedDateUnix = SimpleDateFormat("dd/M/yyyy").parse(pickedDate).time
        viewModelScope.launch {
            expensesRepository.getItemToBuyDao().getAllItems(selectedDateUnix).collect {
                itemToBuyLiveData.postValue(it)
            }
        }
//        viewModelScope.launch {
//            itemToBuyDao.getAllItems(pickedDate).collect {
//                itemToBuyLiveData.postValue(it)
//            }
//        }
    }

    fun deleteItem(itemToBuy: ItemToBuy){
        viewModelScope.launch {
            expensesRepository.getItemToBuyDao().delete(itemToBuy)
        }
    }
}