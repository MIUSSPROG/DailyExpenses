package com.example.dailyexpenses.ui.main.auth

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _childCreationLiveData = MutableLiveData<Child?>()
    val childCreationLiveData: LiveData<Child?> = _childCreationLiveData

    fun createChild(child: Child){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().createChildEncoded(child)
            when(response){
                is ApiResponse.Success ->{
                    _childCreationLiveData.postValue(response.data!!)
                    FirebaseRepository.saveToken(child.login)
                }
                is ApiResponse.Error ->{
                    _childCreationLiveData.postValue(null)
                    Log.d("Error", response.exception.toString())
                }
            }

        }

    }

    fun createParent(parent: Parent){
        viewModelScope.launch {
            expensesRepository.getRemoteDataSource().createParentEncoded(parent)
        }
        FirebaseRepository.saveToken(parent.login)
    }




}