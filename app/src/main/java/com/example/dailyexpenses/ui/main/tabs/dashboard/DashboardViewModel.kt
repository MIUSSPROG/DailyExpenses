package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applandeo.materialcalendarview.EventDay
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.api.Plan
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.HelperMethods
import com.example.dailyexpensespredprof.utils.prefs
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import okhttp3.ResponseBody
import java.lang.Exception
import java.util.*
import kotlin.math.exp

class DashboardViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val _itemToBuyLiveData = MutableLiveData<List<ItemToBuy>>()
    val itemToBuyLiveData: LiveData<List<ItemToBuy>> = _itemToBuyLiveData

    private val _plansLiveData = MutableLiveData<Int>()
    val plansLiveData: LiveData<Int> = _plansLiveData

    private val _childPlansFromServer = MutableLiveData<List<Plan>>()
    val childPlansFromServer: LiveData<List<Plan>> = _childPlansFromServer

    fun getItemsToBuy(pickedDate: Long){
        viewModelScope.launch {
            expensesRepository.getItemToBuyDao().getAllItems(pickedDate).collect {
                _itemToBuyLiveData.postValue(it)
            }
        }
    }

    fun setCalendarEvents(){
        viewModelScope.launch {
            try {
                val events = mutableListOf<EventDay>()
                val calendar = Calendar.getInstance()
                val todayDay = HelperMethods.convertMillisToDate(calendar.timeInMillis).split('/')[0].toInt()

                val plansFromServerDeffered = async{
                    expensesRepository.getRemoteDataSource().getChildPlans(prefs.id)
                }

                val plansFromDBDeffered = async{
                    expensesRepository.getItemToBuyDao().getAllItemsOrderedByDate()
                }

                val plansFromServer = plansFromServerDeffered.await().body()?.plans!!.filter { it.confirm != null }
                val plansFromDB = plansFromDBDeffered.await()

                if (plansFromServer.isNotEmpty() && plansFromDB.isNotEmpty()) {
                    plansFromServer?.forEach { serverPlan ->
                        val planExist = plansFromDB.firstOrNull{plan -> plan.id == serverPlan.dbId}
                        if (planExist != null){
                            if (serverPlan.confirm != null){
                                expensesRepository.getItemToBuyDao().update(planExist.copy(confirm = serverPlan.confirm))
                            }
                        }
                    }
//                    var plansFromServerGroupByDate = mutableMapOf<Long, MutableList<Plan>>()
//                    var plansFromDbGroupByDate = mutableMapOf<Long, MutableList<Plan>>()
//
//                    plansFromDB.forEach { plan ->
//                        val date = plan.date
//                        if (plansFromDbGroupByDate[date] == null){
//                            plansFromDbGroupByDate[date] = mutableListOf()
//                            plansFromDbGroupByDate[date]!!.add(plan.convertToDbPlan(prefs.id))
//                        }else{
//                            plansFromDbGroupByDate[date]!!.add(plan.convertToDbPlan(prefs.id))
//                        }
//                    }
//
//                    plansFromServer.body()?.plans?.forEach { plan ->
//                        val date = plan.date
//                        if (plansFromServerGroupByDate[date] == null) {
//                            plansFromServerGroupByDate[date] = mutableListOf()
//                            plansFromServerGroupByDate[date]!!.add(plan)
//                        } else {
//                            plansFromServerGroupByDate[date]!!.add(plan)
//                        }
//                    }
//                    Log.d("Data DB: ", plansFromDbGroupByDate.toString())
//                    Log.d("Data Server: ", plansFromServerGroupByDate.toString())
//                    Log.d("Data Comparison: ",
//                        (plansFromDbGroupByDate == plansFromServerGroupByDate).toString()
//                    )
//            _childPlansFromServer.postValue(response.body()?.plans)
                }
            }catch (ex: Exception){
                Log.d("Error", ex.message.toString())
            }
        }
    }

    fun sendItemToBuyToParentApproval(pickedDate: Long){
        viewModelScope.launch {
            val itemsToBuyFromDB = expensesRepository.getItemToBuyDao().getAllItemsToSendToParentApproval(pickedDate)
            val plansForApproval = mutableListOf<Plan>()

            itemsToBuyFromDB.filter { !it.send }.forEach { item ->
                val plan = Plan(name = item.name,
                    price = item.price,
                    date = item.date,
                    confirm = item.confirm,
                    categoryId = item.categoryId,
                    childId = prefs.id,
                    dbId = item.id,
                )
                plansForApproval.add(plan)
            }
            val response = expensesRepository.getRemoteDataSource().sendPlansForApproval(plansForApproval)
            when(response){
                is ApiResponse.Success -> {
                    itemsToBuyFromDB.forEach {
                        expensesRepository.getItemToBuyDao().update(it.copy(send = true))
                    }
                    _plansLiveData.postValue(response.data!!)
                }
                is ApiResponse.Error -> {
                    _plansLiveData.postValue(400)
                }
            }

        }
    }

    fun deleteItem(itemToBuy: ItemToBuy){
        viewModelScope.launch {
            coroutineScope {
                expensesRepository.getItemToBuyDao().delete(itemToBuy)
                expensesRepository.getRemoteDataSource().deletePlan(prefs.id)
            }
        }
    }


}