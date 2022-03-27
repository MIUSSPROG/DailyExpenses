package com.example.dailyexpenses.ui.main.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.repository.FirebaseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {



    fun createChild(child: Child){
        viewModelScope.launch {
            expensesRepository.getRemoteDataSource().createChildEncoded(child)
        }
        FirebaseRepository.saveToken(child.login)
    }

    fun createParent(parent: Parent){
        viewModelScope.launch {
            expensesRepository.getRemoteDataSource().createParentEncoded(parent)
        }
        FirebaseRepository.saveToken(parent.login)
    }




}