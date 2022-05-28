package com.example.dailyexpenses.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


//sealed class DaoResponse<T>(
//    data: T? = null,
//    exception: Exception? = null
//){
//    data class Success<T>(val data: T): DaoResponse<T>(data, null)
//    data class Error<T>(val exception: Exception): DaoResponse<T>(null, exception)
//}

sealed class DaoResponse<T>(
    data: T? = null,
    exception: Exception? = null
){
    data class Success<T>(val data: T): DaoResponse<T>(data, null)
    data class Error<T>(val exception: Exception): DaoResponse<T>(null, exception)
}

@Dao
interface ItemToBuyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemToBuy: ItemToBuy)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createUser(user: User): Long

    @Delete
    suspend fun delete(itemToBuy: ItemToBuy)

    @Query("SELECT * FROM ItemToBuy WHERE userRemoteId = :userId ORDER BY date")
    suspend fun getAllItemsOrderedByDate(userId: Int): List<ItemToBuy>

    @Update
    suspend fun update(itemToBuy: ItemToBuy)

    @Query("SELECT * FROM ItemToBuy WHERE date = :pickDate and userRemoteId= :userId")
    suspend fun getAllItems(pickDate: Long, userId: Int): List<ItemToBuy>

//    @Query("SELECT * FROM ItemToBuy WHERE date = :pickDate")
//    suspend fun getAllItemsToSendToParentApproval(pickDate: Long): List<ItemToBuy>

//    @Query("SELECT * FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate")
//    fun getAllItemsInRange(fromDate: Long, toDate: Long): Flow<List<ItemToBuy>>

    @Query("SELECT name FROM ItemToBuy WHERE categoryId = :catId")
    suspend fun getCatNameById(catId: Int): String

    @Query("SELECT date, SUM(price) as 'sumPrice' FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate AND userRemoteId = :userId GROUP BY date")
    fun getAllItemsInRange(userId: Int, fromDate: Long, toDate: Long): Flow<List<HistogramData>>

    @Query("SELECT date, SUM(price) as 'sumPrice' FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate AND userRemoteId = :userId GROUP BY date")
    suspend fun getAllItemsInRange2(userId: Int, fromDate: Long, toDate: Long): List<HistogramData>

    @Query("SELECT category, SUM(price) as 'sumPrice' FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate AND userRemoteId = :userId GROUP BY categoryId")
    fun getAllItemsByCategory(userId: Int, fromDate: Long, toDate: Long): Flow<List<DiagramData>>

    @Query("SELECT category, SUM(price) as 'sumPrice' FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate AND userRemoteId = :userId GROUP BY categoryId")
    suspend fun getAllItemsByCategory2(userId: Int, fromDate: Long, toDate: Long): List<DiagramData>
}