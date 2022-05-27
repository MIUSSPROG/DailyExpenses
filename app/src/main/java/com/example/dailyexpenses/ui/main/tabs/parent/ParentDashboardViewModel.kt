package com.example.dailyexpenses.ui.main.tabs.parent

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.UiState
import com.example.dailyexpenses.utils.prefs
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class ParentDashboardViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _childrenLiveData = MutableLiveData<ParentChildren?>()
    val childrenLiveData: LiveData<ParentChildren?> = _childrenLiveData

    private val _childPlans = MutableLiveData<UiState<List<PlanRV>>>()
    val childPlans: LiveData<UiState<List<PlanRV>>> = _childPlans

    private val _planConfirmation = MutableLiveData<UiState<Boolean>>()
    val planConfirmation: LiveData<UiState<Boolean>> = _planConfirmation

    private val exceptionHandlerFilterPlans = CoroutineExceptionHandler { _, throwable ->
        Log.d("Error", throwable.message.toString())
        _childPlans.postValue(UiState.Error(throwable as Exception))
    }

    fun getChildren(){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getParentChildren(prefs.id)
            when(response){
                is ApiResponse.Success ->{
                    _childrenLiveData.postValue(response.data!!)
                }
                is ApiResponse.Error ->{
                    _childrenLiveData.postValue(null)
                }
            }
        }
    }

    fun changeChildPlanConfirmation(plan: PlanRV, confirmed: Boolean){
        viewModelScope.launch {
            when(expensesRepository.getRemoteDataSource().confirmPlan(plan.id!!, PlanConfirm(confirm = confirmed))){
                is ApiResponse.Success -> {
                    _planConfirmation.postValue(UiState.Success(true))
                }
                is ApiResponse.Error -> {
                    _planConfirmation.postValue(UiState.Error(Exception()))
                }
            }
        }
    }


    fun getChildrenPlans(childId: Int, confirmed: Boolean?, firstUnixTime: Long, secondUnixTime: Long){
        viewModelScope.launch(exceptionHandlerFilterPlans) {
            var response = async { expensesRepository.getRemoteDataSource().getChildPlans(id = childId, fromDateUnix = firstUnixTime, toDateUnix = secondUnixTime, confirmed = confirmed) }
            val categories = async { expensesRepository.getRemoteDataSource().getCategories().associateBy ({it.id}, {it.name}) }

            val plansRV = response.await().body()?.filter { it.date in (firstUnixTime..secondUnixTime)}
                ?.map { plan ->
                    categories.await()[plan.categoryId]?.let {
                        PlanRV(
                            id = plan.id,
                            name = plan.name,
                            price = plan.price,
                            date = plan.date,
                            confirm = plan.confirm,
                            image = plan.image,
                            categoryName = it
                        )
                    }
                }?.sortedBy { it?.date }
            _childPlans.postValue(UiState.Success(plansRV as List<PlanRV>?))
        }
    }
}
