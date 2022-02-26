package com.example.dailyexpenses.ui.main.tabs.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.ItemToBuyDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel @ViewModelInject constructor(
    private val itemToBuyDao: ItemToBuyDao
): ViewModel() {

    val itemToBuyLiveData = MutableLiveData<List<ItemToBuy>>()

    fun getItemsToBuy(pickedDate: String){
        viewModelScope.launch {
            itemToBuyDao.getAllItems(pickedDate).collect {
                itemToBuyLiveData.postValue(it)
            }
        }
    }

    fun deleteItem(itemToBuy: ItemToBuy){
        viewModelScope.launch {
            itemToBuyDao.delete(itemToBuy)
        }
    }
}