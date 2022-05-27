package com.example.dailyexpenses.ui.main.tabs.parent

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.UiState
import com.example.dailyexpenses.utils.prefs
import kotlinx.coroutines.launch
import retrofit2.Response

class ParentProfileViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {
    private val _childrenInvitationsLiveData = MutableLiveData<UiState<ParentChildren>>()
    val childrenInvitationsLiveData: LiveData<UiState<ParentChildren>> = _childrenInvitationsLiveData
    private val _confirmInvitationLiveData = MutableLiveData<UiState<Response<Child>>>()
    val confirmInvitationLiveData: LiveData<UiState<Response<Child>>> = _confirmInvitationLiveData

    fun getChildrenInvitations(parentId: Int){
        viewModelScope.launch {
            when(val response = expensesRepository.getRemoteDataSource().getParentChildren(parentId)){
                is ApiResponse.Success -> {
                    _childrenInvitationsLiveData.postValue(UiState.Success(response.data))
                }
                is ApiResponse.Error -> {
                    _childrenInvitationsLiveData.postValue(UiState.Error(response.exception))
                }
            }
        }
    }

    fun confirmInvitation(child: Child){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().confirmInvitation(child.id, ChildInvitation(
                prefs.id, true))
            when(response){
                is ApiResponse.Success -> {
                    _confirmInvitationLiveData.postValue(UiState.Success(response.data!!))
                }
                is ApiResponse.Error -> {
                    _confirmInvitationLiveData.postValue(UiState.Error(response.exception))
                }
            }
        }
    }
}