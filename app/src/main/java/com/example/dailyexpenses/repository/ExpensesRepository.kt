package com.example.dailyexpenses.repository

import com.example.dailyexpenses.data.ItemToBuyDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpensesRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val daoSource: DaoDataSource,
    private val firebaseDataSource: FirebaseDataSource
) {

    fun getRemoteDataSource() = remoteDataSource

    fun getDaoSource() = daoSource

    fun getFirebaseDataSource() = firebaseDataSource
}