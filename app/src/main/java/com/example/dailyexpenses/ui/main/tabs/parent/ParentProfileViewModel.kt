package com.example.dailyexpenses.ui.main.tabs.parent

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpensespredprof.utils.prefs
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

class ParentProfileViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {
    val childrenInvitationsLiveData = MutableLiveData<Response<ParentChildren>>()
//    val invitationIdLiveData = MutableLiveData<Response<Invitation>>()
    val confirmInvitationLiveData = MutableLiveData<Response<Child>>()

    fun getChildrenInvitations(parentId: Int){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getParentChildren(parentId)
            childrenInvitationsLiveData.postValue(response)
        }
    }

//    fun getInvitationId(parentId: Int, childId: Int){
//        viewModelScope.launch {
//            val response = expensesRepository.getRemoteDataSource().getInvitationId(parentId, childId)
//            invitationIdLiveData.postValue(response)
//        }
//    }

    fun confirmInvitation(child: Child){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().confirmInvitation(child.id, ChildInvitation(prefs.id, true))
            confirmInvitationLiveData.postValue(response)
        }
    }
}