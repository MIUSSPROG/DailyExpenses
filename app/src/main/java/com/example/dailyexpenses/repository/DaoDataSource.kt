package com.example.dailyexpenses.repository

import android.util.Log
import com.example.dailyexpenses.data.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DaoDataSource @Inject constructor(private val itemToBuyDao: ItemToBuyDao) {

    suspend fun saveItemToBuy(itemToBuy: ItemToBuy): DaoResponse<Unit>{
        return try {
            val response = itemToBuyDao.insert(itemToBuy)
            DaoResponse.Success(data = response)
        }
        catch (e: Exception){
            DaoResponse.Error(exception = e)
        }
    }

    suspend fun saveUser(user: User): DaoResponse<Long>{
        return try {
            val response = itemToBuyDao.createUser(user)
            DaoResponse.Success(data = response)
        }
        catch (e: Exception){
            DaoResponse.Error(exception = e)
        }
    }

    suspend fun deleteItemToBuy(itemToBuy: ItemToBuy): DaoResponse<Unit>{
        return try {
            val response = itemToBuyDao.delete(itemToBuy)
            DaoResponse.Success(data = response)
        }
        catch (e: Exception){
            DaoResponse.Error(exception = e)
        }
    }

    suspend fun getAllItemsOrderedByDate(userId: Int): DaoResponse<List<ItemToBuy>>{
        return try {
            val response = itemToBuyDao.getAllItemsOrderedByDate(userId)
            DaoResponse.Success(data = response)
        }
        catch (e: Exception){
            DaoResponse.Error(exception = e)
        }
    }

    suspend fun updateItemToBuy(itemToBuy: ItemToBuy) = itemToBuyDao.update(itemToBuy)

    suspend fun getAllItems(pickedDate: Long, userId: Int) = itemToBuyDao.getAllItems(pickedDate, userId)

    suspend fun getAllItemsInRange(userId: Int, fromDate: Long, toDate: Long): DaoResponse<List<HistogramData>>{
        return try {
            val response = itemToBuyDao.getAllItemsInRange(userId, fromDate, toDate)
            DaoResponse.Success(data = response)
        }
        catch (e: Exception){
            DaoResponse.Error(exception = e)
        }
    }

    suspend fun getAllItemsByCategory(userId: Int, fromDate: Long, toDate: Long): DaoResponse<List<DiagramData>>{
        return try {
            val response = itemToBuyDao.getAllItemsByCategory(userId, fromDate, toDate)
            DaoResponse.Success(data = response)
        }
        catch (e: Exception){
            DaoResponse.Error(exception = e)
        }
    }
}