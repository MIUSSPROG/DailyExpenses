package com.example.dailyexpenses.repository

import com.example.dailyexpenses.data.ItemToBuyDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpensesRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val itemToBuyDao: ItemToBuyDao
) {

    fun getRemoteDataSource() = remoteDataSource

    fun getItemToBuyDao() = itemToBuyDao

}