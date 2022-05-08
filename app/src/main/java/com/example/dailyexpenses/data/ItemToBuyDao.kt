package com.example.dailyexpenses.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemToBuyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemToBuy: ItemToBuy)

    @Delete
    suspend fun delete(itemToBuy: ItemToBuy)

    @Query("SELECT * FROM ItemToBuy ORDER BY date")
    suspend fun getAllItemsOrderedByDate(): List<ItemToBuy>

    @Update
    suspend fun update(itemToBuy: ItemToBuy)

    @Query("SELECT * FROM ItemToBuy WHERE date = :pickDate")
    suspend fun getAllItems(pickDate: Long): List<ItemToBuy>

    @Query("SELECT * FROM ItemToBuy WHERE date = :pickDate")
    suspend fun getAllItemsToSendToParentApproval(pickDate: Long): List<ItemToBuy>

//    @Query("SELECT * FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate")
//    fun getAllItemsInRange(fromDate: Long, toDate: Long): Flow<List<ItemToBuy>>

    @Query("SELECT date, SUM(price) as 'sumPrice' FROM ItemToBuy WHERE date BETWEEN :fromDate AND :toDate GROUP BY date")
    fun getAllItemsInRange(fromDate: Long, toDate: Long): Flow<List<DiagramData>>
}