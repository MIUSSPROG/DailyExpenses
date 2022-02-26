package com.example.dailyexpenses.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemToBuyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(itemToBuy: ItemToBuy)

    @Update
    suspend fun update(itemToBuy: ItemToBuy)

    @Delete
    suspend fun delete(itemToBuy: ItemToBuy)

    @Query("SELECT * FROM ItemToBuy WHERE date = :pickDate")
    fun getAllItems(pickDate: String): Flow<List<ItemToBuy>>
}