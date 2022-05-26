package com.example.dailyexpenses.ui.main.tabs.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.Constants
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

    val parentsLiveData = MutableLiveData<List<Parent>>()
    val fcmLiveData = MutableLiveData<FcmResponse>()
    val checkInvitationLiveData = MutableLiveData<Int>()
    val checkParentLiveData = MutableLiveData<Parent?>()
    val checkChildParentLiveData = MutableLiveData<ChildParent>()
    val cancelInvitationLiveData = MutableLiveData<Child>()
    private val eventChannel = Channel<UiState>()
    val eventFlow = eventChannel.receiveAsFlow()
//    val cancelInvitationStateFlow = MutableStateFlow<Child?>(null)

    fun getParents(){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getParents()
            if (response.isSuccessful && response.body() != null){
                parentsLiveData.postValue(response.body())
            }
        }
    }

    fun checkParent(login: String){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().checkParent(login)
            if (response.isSuccessful && response.body() != null){
                checkParentLiveData.postValue(response.body())
            }
            else{
                checkParentLiveData.postValue(null)
            }
        }
    }

    fun checkChildParent(login: String){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().checkChildParent(login)
            if (response.isSuccessful && response.body() != null){
                checkChildParentLiveData.postValue(response.body())
            }
        }
    }

    suspend fun checkInvitation(parentId: Int): Response<Child> {
        return expensesRepository.getRemoteDataSource().checkInvitation(parentId, prefs.login)
    }

    fun cancelInvitation(){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().cancelInvitation(prefs.id, ChildInvitation(parent = null, confirmed = false))
            if (response.isSuccessful && response.body() != null) {
//                cancelInvitationLiveData.postValue(response.body())
                eventChannel.send(UiState.Success)
            }
        }
    }

    fun sendInvitation(parent: Parent){
        viewModelScope.launch {
            val checkResponse =  async { checkInvitation(parent.id) }.await().code()
            if (checkResponse == 200){
                checkInvitationLiveData.postValue(checkResponse)
            }else {
                val childInvitation = ChildInvitation(parent.id, false)
                val response = expensesRepository.getRemoteDataSource().sendInvitation(prefs.id, childInvitation)
                if (response.isSuccessful && response.body() != null) {
                    FirebaseDatabase.getInstance().reference.child("Tokens").child(parent.login)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val parentToken = snapshot.child("token").value.toString()
                                var notificationSender = NotificationSender(
                                    parentToken,
                                    Notification("Приглашение от ребенка", prefs.login)
                                )
                                viewModelScope.launch {
                                    val firebaseResponse =
                                        expensesRepository.getFirebaseDataSource()
                                            .sendNotification(notificationSender)
                                    if (firebaseResponse.success == 1) {
                                        fcmLiveData.postValue(firebaseResponse)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                }
            }
        }

    }

    sealed class UiState{
        object Success: UiState()
        data class Error(val message: String): UiState()
        object Loading: UiState()
        object Empty: UiState()
    }
}