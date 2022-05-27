package com.example.dailyexpenses.ui.main.auth

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.api.Child
import com.example.dailyexpenses.api.Parent
import com.example.dailyexpenses.data.DaoResponse
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.data.User
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.Exception

class SignInViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
) : ViewModel() {

    private val _userToCheck = MutableLiveData<ApiResponse<Pair<String, Int>>>()
    val userToCheck: LiveData<ApiResponse<Pair<String, Int>>> = _userToCheck

    private val _userCreated = MutableLiveData<UserCreated>()
    val userCreated: LiveData<UserCreated> = _userCreated

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _userToCheck.postValue(ApiResponse.Error(throwable as Exception))
    }

    fun checkUser(login: String, password: String){
        val child = Child(login = login, password = password)
        val parent = Parent(login = login, password = password)
        viewModelScope.launch(exceptionHandler) {
            val childDeffered = async { expensesRepository.getRemoteDataSource().checkChild(child) }
            val parentDeffered = async { expensesRepository.getRemoteDataSource().checkParent(parent) }
            val child = childDeffered.await()
            val parent = parentDeffered.await()
            if (child.isSuccessful && child.body() != null){
                _userToCheck.postValue(ApiResponse.Success(CHILD_ROLE to child.body()!!.id))
            }
            else if (parent.isSuccessful && parent.body() != null){
                _userToCheck.postValue(ApiResponse.Success(PARENT_ROLE to parent.body()!!.id))
            }
            else{
                _userToCheck.postValue(ApiResponse.Error(Exception("Ошибка")))
            }
        }
    }

    fun createUserInDB(userId: Int){
        viewModelScope.launch {
            when(val response = expensesRepository.getDaoSource().saveUser(User(id = userId))){
                is DaoResponse.Success -> {
                    _userCreated.postValue(UserCreated.Success(response))
                }
                is DaoResponse.Error -> {
                    _userCreated.postValue(UserCreated.Error("Ошибка создания пользователя!"))
                }
            }
        }
    }

    sealed class UserCreated{
        data class Success<T>(val data: T?): UserCreated()
        data class Error(val message: String): UserCreated()
    }

    companion object{
        const val CHILD_ROLE = "ребенок"
        const val PARENT_ROLE = "родитель"
    }
}












