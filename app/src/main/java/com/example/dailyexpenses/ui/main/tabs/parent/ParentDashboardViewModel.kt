package com.example.dailyexpenses.ui.main.tabs.parent

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpenses.api.*
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.prefs
import kotlinx.coroutines.launch

class ParentDashboardViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    val childrenLiveData = MutableLiveData<ParentChildren?>()
    val childPlans = MutableLiveData<List<PlanRV>>()
    val planConfirmed = MutableLiveData<Boolean>()
    val planRejected = MutableLiveData<Boolean>()

    fun getChildren(){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getParentChildren(prefs.id)
            when(response){
                is ApiResponse.Success ->{
                    childrenLiveData.postValue(response.data!!)
                }
                is ApiResponse.Error ->{
                    childrenLiveData.postValue(null)
                }
            }
        }
    }

    fun confirmChildPlan(plan: PlanRV){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().confirmPlan(plan.id!!, PlanConfirm(confirm = true))
            when(response){
                is ApiResponse.Success -> {
                    planConfirmed.postValue(true)
                }
                is ApiResponse.Error -> {
                    planConfirmed.postValue(false)
                }
            }
        }
    }

    fun rejectChildPlan(plan: PlanRV){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().confirmPlan(plan.id!!, PlanConfirm(confirm = false))
            when(response){
                is ApiResponse.Success -> {
                    planRejected.postValue(true)
                }
                is ApiResponse.Error -> {
                    planRejected.postValue(false)
                }
            }
        }
    }

    fun filterPlans(childId: Int, confirmed: Boolean){
        viewModelScope.launch {
            val response = expensesRepository.getRemoteDataSource().getFilteredPlans(confirmed, childId)
            when(response){
                is  ApiResponse.Success -> {
                    val categories = expensesRepository.getRemoteDataSource().getCategories()
                    val categoryMap = categories.associateBy ({it.id}, {it.name})
                    val plansRV = mutableListOf<PlanRV>()
                    response.data.forEach {
                        val planRV =
                            categoryMap[it.categoryId]?.let { it1 ->
                                PlanRV(
                                    id = it.id,
                                    name = it.name,
                                    price = it.price,
                                    date = it.date,
                                    confirm = it.confirm,
                                    image = it.image,
                                    categoryName = it1
                                )
                            }
                        plansRV.add(planRV!!)
                    }
                    childPlans.postValue(plansRV)
                }
            }
        }
    }

    fun getChildrenPlans(childId: Int){
        viewModelScope.launch {
            val categories = expensesRepository.getRemoteDataSource().getCategories()
            val categoryMap = categories.associateBy ({it.id}, {it.name})
            val response = expensesRepository.getRemoteDataSource().getChildPlans(childId)
            val plansRV = mutableListOf<PlanRV>()
            response.body()?.plans?.forEach {
                val planRV =
                    categoryMap[it.categoryId]?.let { it1 ->
                        PlanRV(
                            id = it.id,
                            name = it.name,
                            price = it.price,
                            date = it.date,
                            confirm = it.confirm,
                            image = it.image,
                            categoryName = it1
                        )
                    }
                plansRV.add(planRV!!)
                }
            childPlans.postValue(plansRV)
            }
        }
}
