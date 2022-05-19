package com.example.dailyexpenses.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ItemToBuy::class, User::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class ItemToBuyDatabase: RoomDatabase() {

    abstract fun itemToBuyDao(): ItemToBuyDao
}