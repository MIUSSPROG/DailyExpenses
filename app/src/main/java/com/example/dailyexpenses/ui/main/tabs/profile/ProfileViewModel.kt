package com.example.dailyexpenses.ui.main.tabs.profile

import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpensespredprof.utils.prefs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ProfileViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

//    val parents = expensesRepository.getRemoteDataSource().getParents().asLiveData()
    val parentsLiveData = MutableLiveData<List<Parent>>()
    val fcmLiveData = MutableLiveData<FcmResponse>()
    val errorInvitation = MutableLiveData<Response<Invitation>>()

    fun getParents(){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getParents()
            parentsLiveData.postValue(response.body())
        }
    }

    fun sendInvitation(parent: Parent){
        viewModelScope.launch {
            val invitation = Invitation(child = prefs.id, parent = parent.id, confirm = false)
            val response = expensesRepository.getRemoteDataSource().sendInvitation(invitation)
            if (response.isSuccessful && response.body() != null) {
                FirebaseDatabase.getInstance().reference.child("Tokens").child(parent.login)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val parentToken = snapshot.child("token").value.toString()
                            var notificationSender = NotificationSender(parentToken, Notification("Приглашение от ребенка", prefs.login))
                            viewModelScope.launch {
                                val firebaseResponse = expensesRepository.getFirebaseDataSource().sendNotification(notificationSender)
                                if (firebaseResponse.success == 1){
                                    fcmLiveData.postValue(firebaseResponse)
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
            else{
                errorInvitation.postValue(response)
            }
        }


    }
}