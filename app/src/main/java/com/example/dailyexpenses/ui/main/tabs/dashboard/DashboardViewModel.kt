package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applandeo.materialcalendarview.EventDay
import com.example.dailyexpenses.R
import com.example.dailyexpenses.api.ApiResponse
import com.example.dailyexpenses.api.Plan
import com.example.dailyexpenses.data.DaoResponse
import com.example.dailyexpenses.data.ItemToBuy
import com.example.dailyexpenses.repository.ExpensesRepository
import com.example.dailyexpenses.utils.HelperMethods
import com.example.dailyexpenses.utils.UiState
import com.example.dailyexpenses.utils.prefs
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*
import kotlin.Exception

class DashboardViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

//    private val _itemToBuyLiveData = MutableLiveData<List<ItemToBuy>>()
//    val itemToBuyLiveData: LiveData<List<ItemToBuy>> = _itemToBuyLiveData

    private val _plansLiveData = MutableLiveData<Int>()
    val plansLiveData: LiveData<Int> = _plansLiveData

    private val _plansFlow = MutableStateFlow<DashboardUiState>(DashboardUiState.Empty)
    val planFlow: StateFlow<DashboardUiState> = _plansFlow

    private val plansChannel = Channel<DashboardUiState>()
    val plansChannelFlow = plansChannel.receiveAsFlow()

    private val _calendarEvents = MutableLiveData<List<EventDay>>()
    val calendarEvents: LiveData<List<EventDay>> = _calendarEvents

    private val _itemsToBuy = MutableLiveData<DashboardUiState>()
    val itemsToBuy: LiveData<DashboardUiState> = _itemsToBuy

    private val _deletedItem = MutableLiveData<DashboardUiState>()
    val deletedItem: LiveData<DashboardUiState> = _deletedItem

    fun getItemsToBuy(pickedDate: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val plansFromServerDeffered = async{
                expensesRepository.getRemoteDataSource().getChildPlans(id = prefs.id, fromDateUnix = pickedDate, toDateUnix = pickedDate)
            }

            val plansFromDBDeffered = async{
                expensesRepository.getItemToBuyDao().getAllItems(userId = prefs.id, pickDate = pickedDate )
            }

            val plansFromServer = plansFromServerDeffered.await().body()
            val plansFromDB = plansFromDBDeffered.await()

            plansFromServer?.forEach { serverPlan ->
                val planExist = plansFromDB.firstOrNull{ plan -> plan.id == serverPlan.dbId }
                if (planExist != null){
                    if (serverPlan.confirm != null){
                        expensesRepository.getItemToBuyDao()
                            .update(planExist.copy(confirm = serverPlan.confirm))
                    }
                }
            }

            val res = expensesRepository.getItemToBuyDao().getAllItems(userId = prefs.id, pickDate = pickedDate)
            _itemsToBuy.postValue(DashboardUiState.Success(data = res))
        }
    }


    fun setCalendarEvents(curMonth: Int){
        viewModelScope.launch {
            try {
                val events = mutableListOf<EventDay>()

                val plansFromDB = expensesRepository.getItemToBuyDao().getAllItemsOrderedByDate(prefs.id)

                if (plansFromDB.isNotEmpty()) {
                    var plansFromDbGroupByDate = mutableMapOf<Int, MutableList<ItemToBuy>>()

                    plansFromDB.forEach { plan ->
                        val month = HelperMethods.convertMillisToDate(plan.date).split('/')[1].toInt()
                        val date = HelperMethods.convertMillisToDate(plan.date).split('/')[0].toInt()
                        if (month == curMonth) {
                            if (plansFromDbGroupByDate[date] == null) {
                                plansFromDbGroupByDate[date] = mutableListOf()
                                plansFromDbGroupByDate[date]!!.add(plan)
                            } else {
                                plansFromDbGroupByDate[date]!!.add(plan)
                            }
                        }
                    }

                    plansFromDbGroupByDate.forEach {
                        var calendar = Calendar.getInstance()
                        calendar.set(2022, curMonth-1, 1)
                        calendar.add(Calendar.DAY_OF_MONTH, it.key-2)
                        var confirmed = 0
                        var rejected = 0
                        var send = 0
                        it.value.forEach { itemToBuy ->
                            if (itemToBuy.confirm == true){
                                confirmed ++
                            }
                            else if (itemToBuy.confirm == false){
                                rejected ++
                            }
                            if (itemToBuy.send){
                                send ++
                            }
                        }
                        if (confirmed == it.value.size){
                            events.add(EventDay(calendar, R.drawable.ic_done))
                        }
                        else if (rejected > 0){
                            events.add(EventDay(calendar, R.drawable.ic_cancel))
                        }
                        else if (send != it.value.size){
                            events.add(EventDay(calendar, R.drawable.ic_priority))
                        }
                        else if (send > 0){
                            events.add(EventDay(calendar, R.drawable.ic_sync))
                        }
                    }
                    _calendarEvents.postValue(events)
                }
            }catch (ex: Exception){
                Log.d("Error", ex.message.toString())
            }
        }
    }

    fun sendItemToBuyForParentApproval(pickedDate: Long) = viewModelScope.launch{
        try {
            val itemsToBuyFromDB = expensesRepository.getDaoSource().getAllItems(pickedDate, prefs.id)

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
            if (plansForApproval.size != 0) {
                val response =
                    expensesRepository.getRemoteDataSource().sendPlansForApproval(plansForApproval)
                when (response) {
                    is ApiResponse.Success -> {
                        val sendedPlans = response.data
                        itemsToBuyFromDB.filter { !it.send }.forEachIndexed{ index, item ->
                            expensesRepository.getItemToBuyDao().update(item.copy(send = true, remoteDbId = sendedPlans[index].id))
                        }
                        plansChannel.send(UiState.Success())
                    }
                    is ApiResponse.Error -> {
                        plansChannel.send(UiState.Error(Exception("Некорректные данные")))
                    }
                }
            }
            else{
                plansChannel.send(UiState.Error(Exception("Все данные уже были отправлены")))
            }
        }catch (e: Exception){
            plansChannel.send(UiState.Error(Exception("Ошибка получения данных!")))
            Log.d("Error", e.message.toString())
        }
    }

    fun deleteItem(itemToBuy: ItemToBuy){
        viewModelScope.launch {
            if (itemToBuy.send) {
                val response = expensesRepository.getRemoteDataSource().deletePlan(itemToBuy.remoteDbId!!)
                when(response){
                    is ApiResponse.Success -> {
//                        _deletedItem.postValue(UiState.Success())
                        deleteItemFromLocalDb(itemToBuy)
                    }
                    is ApiResponse.Error -> {
                        _deletedItem.postValue(UiState.Error(Exception("Ошибка удаления с сервера")))
                    }
                }
            }
            else{
                deleteItemFromLocalDb(itemToBuy)
            }
        }
    }

    suspend fun deleteItemFromLocalDb(itemToBuy: ItemToBuy){
        val deleteResponse = expensesRepository.getDaoSource().deleteItemToBuy(itemToBuy)
        when(deleteResponse){
            is DaoResponse.Success -> {
                _deletedItem.postValue(UiState.Success())
            }
            is DaoResponse.Error -> {
                _deletedItem.postValue(UiState.Error(Exception("Ошибка удаления из локальной бд")))
            }
        }
    }

//    sealed class DashboardUiState{
//        data class Success<T>(val data: T? = null): DashboardUiState()
//        data class Error(val message: String): DashboardUiState()
//        object Empty: DashboardUiState()
//    }

}