package com.example.dailyexpenses.ui.main.tabs.dashboard

import android.graphics.Bitmap
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
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import kotlin.Exception

class DashboardViewModel @ViewModelInject constructor(
    private val expensesRepository: ExpensesRepository
): ViewModel() {

    private val plansChannel = Channel<UiState<Nothing>>()
    val plansChannelFlow = plansChannel.receiveAsFlow()

    private val _calendarEvents = MutableLiveData<List<EventDay>>()
    val calendarEvents: LiveData<List<EventDay>> = _calendarEvents

    private val _itemsToBuy = MutableLiveData<UiState<List<ItemToBuy>>>()
    val itemsToBuy: LiveData<UiState<List<ItemToBuy>>> = _itemsToBuy

    private val _deletedItemChannel = Channel<UiState<Nothing>>()
    val deletedItemChannelFlow = _deletedItemChannel.receiveAsFlow()

    private val _updatedItem = MutableLiveData<UiState<Nothing>>()
    val updatedItem: LiveData<UiState<Nothing>> = _updatedItem

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _itemsToBuy.postValue(UiState.Error(throwable as Exception))
        Log.d("Error", throwable.message.toString())
    }

    private val exceptionSendImageHandler = CoroutineExceptionHandler { _, throwable ->
        _updatedItem.postValue(UiState.Error(exception = Exception("Ошибка")))
        Log.d("Error", throwable.message.toString())
    }

    fun getItemsToBuy(pickedDate: Long){
        viewModelScope.launch(exceptionHandler){
            val plansFromServerDeffered = async{
                expensesRepository.getRemoteDataSource().getChildPlans(id = prefs.id,  fromDateUnix = pickedDate, toDateUnix = pickedDate)
            }

            val plansFromDBDeffered = async{
                expensesRepository.getDaoSource().getAllItems(userId = prefs.id, pickedDate = pickedDate)
            }

            val plansFromServer = plansFromServerDeffered.await().body()
            val plansFromDB = plansFromDBDeffered.await()


            plansFromServer?.forEach { serverPlan ->
                val planExist = plansFromDB.firstOrNull{ plan -> plan.id == serverPlan.dbId }
                if (planExist != null){
                    if (serverPlan.confirm != null){
                        expensesRepository.getDaoSource()
                            .updateItemToBuy(planExist.copy(confirm = serverPlan.confirm))
                    }
                }
            }

            val res = expensesRepository.getDaoSource().getAllItems(userId = prefs.id, pickedDate = pickedDate)
            _itemsToBuy.postValue(UiState.Success(data = res))
        }
    }


    fun setCalendarEvents(curMonth: Int){
        viewModelScope.launch {
            when(val plansFromDB = expensesRepository.getDaoSource().getAllItemsOrderedByDate(prefs.id)){
                is DaoResponse.Success -> {
                    if (plansFromDB.data.isNotEmpty()) {
                        val events = mutableListOf<EventDay>()

                        var plansFromDbGroupByDate = mutableMapOf<Int, MutableList<ItemToBuy>>()

                        plansFromDB.data.forEach { plan ->
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
                }
            }
        }
    }

    fun sendItemToBuyForParentApproval(pickedDate: Long) = viewModelScope.launch{

        val itemsToBuyFromDB = expensesRepository.getDaoSource().getAllItems(pickedDate, prefs.id)
        val plansForApproval = itemsToBuyFromDB.filter { !it.send }.map {
            Plan(name = it.name,
                price = it.price,
                date = it.date,
                confirm = it.confirm,
                categoryId = it.categoryId,
                childId = prefs.id,
                dbId = it.id,
            )
        }

        if (plansForApproval.isNotEmpty()) {
            val response = expensesRepository.getRemoteDataSource().sendPlansForApproval(plansForApproval)
            when (response) {
                is ApiResponse.Success -> {
                    val sendedPlans = response.data
                    itemsToBuyFromDB.filter { !it.send }.forEachIndexed{ index, item ->
                        expensesRepository.getDaoSource().updateItemToBuy(item.copy(send = true, remoteDbId = sendedPlans[index].id))
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

    }

    fun deleteItem(itemToBuy: ItemToBuy){
        viewModelScope.launch {
            if (itemToBuy.send) {
                when(expensesRepository.getRemoteDataSource().deletePlan(itemToBuy.remoteDbId!!)){
                    is ApiResponse.Success -> {
                        deleteItemFromLocalDb(itemToBuy)
                    }
                    is ApiResponse.Error -> {
                        _deletedItemChannel.send(UiState.Error(Exception("Ошибка удаления с сервера!")))
                    }
                }
            }
            else{
                deleteItemFromLocalDb(itemToBuy)
            }
        }
    }

    fun updateItem(itemToBuy: ItemToBuy){
        viewModelScope.launch(exceptionSendImageHandler) {
            var imagePart: MultipartBody.Part? = null
            if (itemToBuy != null){
                val file = File(itemToBuy.imageUri)
                imagePart = MultipartBody.Part.createFormData("image", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))

            }
            coroutineScope {
                expensesRepository.getDaoSource().updateItemToBuy(itemToBuy) }
                expensesRepository.getRemoteDataSource().saveImageForPlan(itemToBuy.remoteDbId!!, imagePart)
            }
        _updatedItem.postValue(UiState.Success())
        }

    private suspend fun deleteItemFromLocalDb(itemToBuy: ItemToBuy){
        when(expensesRepository.getDaoSource().deleteItemToBuy(itemToBuy)){
            is DaoResponse.Success -> {
                _deletedItemChannel.send(UiState.Success())
            }
            is DaoResponse.Error -> {
                _deletedItemChannel.send(UiState.Error(Exception("Ошибка удаления из локальной бд!")))
            }
        }
    }


}