package com.example.dailyexpenses.ui.main.tabs.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

//    val parents = expensesRepository.getRemoteDataSource().getParents().asLiveData()
    val parentsLiveData = MutableLiveData<List<Parent>>()

    fun getParents(){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getParents()
            parentsLiveData.postValue(response.body())
        }
    }
}