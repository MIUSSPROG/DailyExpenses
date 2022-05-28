package com.example.dailyexpenses.ui.main.tabs.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.Constants
import com.example.dailyexpenses.utils.UiState
import com.example.dailyexpenses.utils.prefs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _parentsLiveData = MutableLiveData<UiState<List<Parent>>>()
    val parentsLiveData: LiveData<UiState<List<Parent>>> = _parentsLiveData

    private val _fcmLiveData = MutableLiveData<UiState<FcmResponse>>()
    val fcmLiveData: LiveData<UiState<FcmResponse>> = _fcmLiveData

    private val _checkInvitationLiveData = MutableLiveData<UiState<Child>>()
    val checkInvitationLiveData: LiveData<UiState<Child>> = _checkInvitationLiveData

    private val _checkParentLiveData = MutableLiveData<UiState<Parent>>()
    val checkParentLiveData: LiveData<UiState<Parent>> = _checkParentLiveData

    private val _checkChildParentLiveData = MutableLiveData<UiState<ChildParent>>()
    val checkChildParentLiveData: LiveData<UiState<ChildParent>> = _checkChildParentLiveData

    private val _checkChildParentChannel = Channel<UiState<Nothing>>()
    val checkChildParentChannelFlow = _checkChildParentChannel.receiveAsFlow()

    fun getParents(){
        viewModelScope.launch {
            when(val response = expensesRepository.getRemoteDataSource().getParents()){
                is ApiResponse.Success -> {
                    _parentsLiveData.postValue(UiState.Success(response.data))
                }
                is ApiResponse.Error -> {
                    _parentsLiveData.postValue(UiState.Error(response.exception))
                }
            }
        }
    }

    fun checkParent(login: String){
        viewModelScope.launch {
            when(val response = expensesRepository.getRemoteDataSource().checkParent(login)){
                is ApiResponse.Success -> {
                    _checkParentLiveData.postValue(UiState.Success(response.data))
                }
                is ApiResponse.Error -> {
                    _checkParentLiveData.postValue(UiState.Error(response.exception))
                }
            }
        }
    }

    fun checkChildParent(login: String){
        viewModelScope.launch {
            when(val response = expensesRepository.getRemoteDataSource().checkChildParent(login)){
                is ApiResponse.Success -> {
                    _checkChildParentLiveData.postValue(UiState.Success(response.data))
                }
                is ApiResponse.Error -> {
                    _checkChildParentLiveData.postValue(UiState.Error(response.exception))
                }
            }
        }
    }

    fun cancelInvitation(){
        viewModelScope.launch {
            when(val response = expensesRepository.getRemoteDataSource().cancelInvitation(prefs.id, ChildInvitation(parent = null, confirmed = false))){
                is ApiResponse.Success -> {
                    _checkChildParentChannel.send(UiState.Success())
                }
                is ApiResponse.Error -> {
                    _checkChildParentChannel.send(UiState.Error(response.exception))
                }
            }
        }
    }

    fun sendNotification(notificationSender: NotificationSender){
        viewModelScope.launch {
            val firebaseResponse =
                expensesRepository.getFirebaseDataSource()
                    .sendNotification(notificationSender)
            when(firebaseResponse){
                is ApiResponse.Success -> {
                    _fcmLiveData.postValue(UiState.Success(firebaseResponse.data))
                }
                is ApiResponse.Error -> {
                    _fcmLiveData.postValue(UiState.Error(firebaseResponse.exception))
                }
            }
        }
    }

    fun sendInvitation(parent: Parent){
        viewModelScope.launch {
            val childInvitation = ChildInvitation(parent.id, false)
            when (expensesRepository.getRemoteDataSource().sendInvitation(prefs.id, childInvitation)) {
                is ApiResponse.Success -> {
                    FirebaseDatabase.getInstance().reference.child("Tokens").child(parent.login)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val parentToken = snapshot.child("token").value.toString()
                                var notificationSender = NotificationSender(
                                    parentToken,
                                    Notification("Приглашение от ребенка", prefs.login))
                                sendNotification(notificationSender)
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }
            }
        }
    }

    fun checkInvitation(parent: Parent){
        viewModelScope.launch {
            when(val checkResponse =  expensesRepository.getRemoteDataSource().checkInvitation(parent.id, prefs.login)){
                is ApiResponse.Success -> {
                    _checkInvitationLiveData.postValue(UiState.Success(checkResponse.data))
                }
                is ApiResponse.Error -> {
                    _checkInvitationLiveData.postValue(UiState.Error(checkResponse.exception))
                }
            }
        }
    }
}