package com.example.dailyexpenses.ui.main.auth

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignInViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
) : ViewModel() {

    val childToCheck = MutableLiveData<Child?>()
    val parentToCheck = MutableLiveData<Parent?>()

    fun checkChild(child: Child) {
        viewModelScope.launch {
            val res = expensesRepository.getRemoteDataSource().checkChild(child)
            if (res.isSuccessful) {
                childToCheck.postValue(res.body())
            } else {
                Log.d("Error", res.errorBody().toString())
                childToCheck.postValue(null)
            }
        }
    }

    fun checkParent(parent: Parent) {
        viewModelScope.launch {
            val res = expensesRepository.getRemoteDataSource().checkParent(parent)
            if (res.isSuccessful) {
                parentToCheck.postValue(res.body())
            } else {
                Log.d("Error", res.errorBody().toString())
                parentToCheck.postValue(null)
            }
        }
    }
}