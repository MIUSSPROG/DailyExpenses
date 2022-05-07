package com.example.dailyexpenses.ui.main.auth

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
) : ViewModel() {

    private val _childToCheck = MutableLiveData<Child?>()
    val childToCheck: LiveData<Child?> = _childToCheck

    private val _parentToCheck = MutableLiveData<Parent?>()
    val parentToCheck: LiveData<Parent?> = _parentToCheck

    val pairMediatorLiveData = PairMediatorLiveData(_childToCheck, _parentToCheck)

    private val _childToCheckFlow = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val childToCheckFlow: StateFlow<LoginUiState> = _childToCheckFlow

    private val _parentToCheckFlow = MutableStateFlow<LoginUiState>(LoginUiState.Empty)
    val parentToCheckFlow: StateFlow<LoginUiState> = _parentToCheckFlow

    private val _userToCheck = MutableLiveData<Pair<String, Int>?>()
    val userToCheck: LiveData<Pair<String, Int>?> = _userToCheck

    fun checkUser(login: String, password: String){
        val child = Child(login = login, password = password)
        val parent = Parent(login = login, password = password)
        viewModelScope.launch {
            val childDeffered = async { expensesRepository.getRemoteDataSource().checkChild(child) }
            val parentDeffered = async { expensesRepository.getRemoteDataSource().checkParent(parent) }
            val child = childDeffered.await()
            val parent = parentDeffered.await()
            if (child.isSuccessful && child.body() != null){
                _userToCheck.postValue(CHILD_ROLE to child.body()!!.id)
            }
            else if (parent.isSuccessful && parent.body() != null){
                _userToCheck.postValue(PARENT_ROLE to parent.body()!!.id)
            }
            else{
                _userToCheck.postValue(null)
            }
        }
    }

    fun checkChild(child: Child) {
        viewModelScope.launch {
            val res = expensesRepository.getRemoteDataSource().checkChild(child)
            if (res.isSuccessful) {
                _childToCheck.postValue(res.body())
            } else {
                Log.d("Error", res.errorBody().toString())
                _childToCheck.postValue(null)
            }
        }
    }

    fun checkChildFlow(child: Child) = viewModelScope.launch {
        _childToCheckFlow.value = LoginUiState.Loading
        val res = expensesRepository.getRemoteDataSource().checkChild(child)
        if (res.isSuccessful){
            _childToCheckFlow.value = LoginUiState.Success(data = res.body())
        }else{
            Log.d("Error", res.errorBody().toString())
            _childToCheckFlow.value = LoginUiState.Error(message = res.errorBody().toString())
        }
    }

    fun checkParentFlow(parent: Parent) = viewModelScope.launch {
        _parentToCheckFlow.value = LoginUiState.Loading
        val res = expensesRepository.getRemoteDataSource().checkParent(parent)
        if (res.isSuccessful) {
            _parentToCheckFlow.value = LoginUiState.Success(data = res.body())
        } else {
            Log.d("Error", res.errorBody().toString())
            _parentToCheckFlow.value = LoginUiState.Error(message = res.errorBody().toString())
        }
    }


    fun checkParent(parent: Parent) {
        viewModelScope.launch {
            val res = expensesRepository.getRemoteDataSource().checkParent(parent)
            if (res.isSuccessful) {
                _parentToCheck.postValue(res.body())
            } else {
                Log.d("Error", res.errorBody().toString())
                _parentToCheck.postValue(null)
            }
        }
    }

    sealed class LoginUiState{
        data class Success<T>(val data: T?): LoginUiState()
        data class Error(val message: String): LoginUiState()
        object Loading: LoginUiState()
        object Empty: LoginUiState()
    }

    class PairMediatorLiveData<F, S>(firstLiveData: LiveData<F>, secondLiveData: LiveData<S>): MediatorLiveData<Pair<F?, S?>>(){
        init {
            addSource(firstLiveData){ firstLiveDataValue: F -> value = firstLiveDataValue to secondLiveData.value}
            addSource(secondLiveData){ secondLiveDataValue: S -> value = firstLiveData.value to secondLiveDataValue}
        }
    }

    companion object{
        const val CHILD_ROLE = "ребенок"
        const val PARENT_ROLE = "родитель"
    }
}












