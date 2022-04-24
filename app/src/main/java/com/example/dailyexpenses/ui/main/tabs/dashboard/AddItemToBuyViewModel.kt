package com.example.dailyexpenses.ui.main.tabs.dashboard

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.Category
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.ItemToBuyDao
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.launch

class AddItemToBuyViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _categoriesLiveData = MutableLiveData<List<Category>>()
    val categoriesLiveData: LiveData<List<Category>> = _categoriesLiveData

    fun saveItemToBuy(itemToBuy: ItemToBuy) = viewModelScope.launch {
        expensesRepository.getItemToBuyDao().insert(itemToBuy)
    }

    fun getCategories() = viewModelScope.launch {
        val response = expensesRepository.getRemoteDataSource().getCategories()
        _categoriesLiveData.postValue(response)
//        if (response.isSuccessful && response.body() != null){
//            _categoriesLiveData.postValue(response.body())
//        }
    }
}