package com.example.dailyexpenses.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ItemToBuy::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
    )
abstract class ItemToBuyDatabase: RoomDatabase() {

    abstract fun itemToBuyDao(): ItemToBuyDao
}