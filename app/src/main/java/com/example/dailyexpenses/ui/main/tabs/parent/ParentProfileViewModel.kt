package com.example.dailyexpenses.ui.main.tabs.parent

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.prefs
import kotlinx.coroutines.launch
import retrofit2.Response

class ParentProfileViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {
    val childrenInvitationsLiveData = MutableLiveData<ParentChildren?>()
    val confirmInvitationLiveData = MutableLiveData<Response<Child>>()

    fun getChildrenInvitations(parentId: Int){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getParentChildren(parentId)
            when(response){
                is ApiResponse.Success -> {
                    childrenInvitationsLiveData.postValue(response.data!!)
                }
                is ApiResponse.Error -> {
                    childrenInvitationsLiveData.postValue(null)
                }
            }
        }
    }

    fun confirmInvitation(child: Child){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().confirmInvitation(child.id, ChildInvitation(
                prefs.id, true))
            confirmInvitationLiveData.postValue(response)
        }
    }
}