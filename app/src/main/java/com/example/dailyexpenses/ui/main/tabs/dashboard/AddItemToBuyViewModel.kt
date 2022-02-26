package com.example.dailyexpenses.ui.main.tabs.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.ItemToBuyDao
import kotlinx.coroutines.launch

class AddItemToBuyViewModel @ViewModelInject constructor(
    private val itemToBuyDao: ItemToBuyDao
): ViewModel() {

    fun saveItemToBuy(itemToBuy: ItemToBuy) = viewModelScope.launch {
        itemToBuyDao.insert(itemToBuy)
    }
}