package com.example.dailyexpenses.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ItemToBuy::class], version = 3)
abstract class ItemToBuyDatabase: RoomDatabase() {

    abstract fun itemToBuyDao(): ItemToBuyDao
}