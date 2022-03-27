package com.example.dailyexpenses.ui.main.tabs.parent

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.ChildOfParent
import com.example.dailyexpenses.api.Invitation
import com.example.dailyexpenses.repository.ExpensesRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class ParentProfileViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {
    val childrenInvitationsLiveData = MutableLiveData<Response<List<ChildOfParent>>>()
    val invitationIdLiveData = MutableLiveData<Response<Invitation>>()
    val confirmInvitationLiveData = MutableLiveData<ResponseBody>()

    fun getChildrenInvitations(parentId: Int){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getChildrenInvitations(parentId)
            childrenInvitationsLiveData.postValue(response)
        }
    }

    fun getInvitationId(parentId: Int, childId: Int){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getInvitationId(parentId, childId)
            invitationIdLiveData.postValue(response)
        }
    }

    fun confirmInvitation(id: Int, invitation: Invitation){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().confirmInvitation(id, invitation)
            confirmInvitationLiveData.postValue(response)
        }
    }
}