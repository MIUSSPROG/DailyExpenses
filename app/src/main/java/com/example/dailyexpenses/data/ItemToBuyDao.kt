package com.example.dailyexpenses.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM ItemToBuy WHERE date = :pickDate")
    suspend fun getAllItemsToSendToParentApproval(pickDate: Long): List<ItemToBuy>

//    @Query("SELECT * FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate")
//    fun getAllItemsInRange(fromDate: Long, toDate: Long): Flow<List<ItemToBuy>>

    @Query("SELECT name FROM ItemToBuy WHERE categoryId = :catId")
    suspend fun getCatNameById(catId: Long): String

    @Query("SELECT date, SUM(price) as 'sumPrice' FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate AND userRemoteId = :userId GROUP BY date")
    fun getAllItemsInRange(userId: Int, fromDate: Long, toDate: Long): Flow<List<HistogramData>>

    @Query("SELECT categoryId, SUM(price) as 'sumPrice' FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate AND userRemoteId = :userId GROUP BY categoryId")
    fun getAllItemsByCategory(userId: Int, fromDate: Long, toDate: Long): Flow<List<DiagramData>>
}