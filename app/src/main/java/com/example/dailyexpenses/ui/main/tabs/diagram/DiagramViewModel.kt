package com.example.dailyexpenses.ui.main.tabs.diagram

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.launch

class DiagramViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    fun createChild(child: Child){
        viewModelScope.launch {
            expensesRepository.getRemoteDataSource().createChild(child)
        }
    }

//    fun sharePlans()

}