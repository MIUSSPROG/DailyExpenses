package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.api.Category
import com.example.dailyexpenses.data.DaoResponse
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.ItemToBuyDao
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class AddItemToBuyViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _categoriesLiveData = MutableLiveData<UiState<List<Category>>>()
    val categoriesLiveData: LiveData<UiState<List<Category>>> = _categoriesLiveData

    private val _savedItemToBuy = MutableLiveData<UiState<Nothing>>()
    val savedItemToBuy: LiveData<UiState<Nothing>> = _savedItemToBuy

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _categoriesLiveData.postValue(UiState.Error(exception = Exception("Ошибка")))
        Log.d("Error", throwable.message.toString())
    }

    fun saveItemToBuy(itemToBuy: ItemToBuy) = viewModelScope.launch {
        when(expensesRepository.getDaoSource().saveItemToBuy(itemToBuy)){
            is DaoResponse.Success -> {
                _savedItemToBuy.postValue(UiState.Success())
            }
            is DaoResponse.Error -> {
                _savedItemToBuy.postValue(UiState.Error(Exception("Ошибка")))
            }
        }
    }

    fun getCategories() = viewModelScope.launch(exceptionHandler) {
        val response = expensesRepository.getRemoteDataSource().getCategories()
        _categoriesLiveData.postValue(UiState.Success(data = response))
    }
}