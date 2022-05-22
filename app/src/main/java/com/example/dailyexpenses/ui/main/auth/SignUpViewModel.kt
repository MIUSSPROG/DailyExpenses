package com.example.dailyexpenses.ui.main.auth

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.data.User
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _childCreationLiveData = MutableLiveData<Child?>()
//    val childCreationLiveData: LiveData<Child?> = _childCreationLiveData

    private val _parentCreationLiveData = MutableLiveData<Parent?>()
//    val parentCreationLiveData: LiveData<Parent?> = _parentCreationLiveData

    val userCreatedPairMediatorLiveData = PairMediatorLiveData(_childCreationLiveData, _parentCreationLiveData)

    fun createChild(child: Child){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().createChildEncoded(child)
            when(response){
                is ApiResponse.Success -> {
                    _childCreationLiveData.postValue(response.data!!)
                    FirebaseRepository.saveToken(child.login)
                    expensesRepository.getItemToBuyDao().createUser(User(response.data.id))
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
            val response = expensesRepository.getRemoteDataSource().createParentEncoded(parent)
            when(response){
                is ApiResponse.Success -> {
                    _parentCreationLiveData.postValue(response.data!!)
                    FirebaseRepository.saveToken(parent.login)
                    expensesRepository.getItemToBuyDao().createUser(User(response.data.id))
                }
                is ApiResponse.Error -> {
                    _parentCreationLiveData.postValue(null)
                    Log.d("Error", response.exception.toString())
                }
            }
        }
    }


    class PairMediatorLiveData<F, S>(firstLiveData: LiveData<F>, secondLiveData: LiveData<S>): MediatorLiveData<Pair<F?, S?>>(){
        init {
            addSource(firstLiveData){ firstLiveDataValue: F -> value = firstLiveDataValue to secondLiveData.value}
            addSource(secondLiveData){ secondLiveDataValue: S -> value = firstLiveData.value to secondLiveDataValue}
        }
    }

}