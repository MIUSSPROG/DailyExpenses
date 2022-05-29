package com.example.dailyexpenses.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dailyexpenses.migrations.MigrationDeleteColumnLoginInItemTuBuy
import com.example.dailyexpenses.migrations.MigrationDeleteTableUser

@Database(
    entities = [ItemToBuy::class, User::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 2, to = 3),
//        AutoMigration(from = 3, to = 4, spec = MigrationDeleteColumnLoginInItemTuBuy::class),
//        AutoMigration(from = 3, to = 4, spec = MigrationDeleteTableUser::class),
////        AutoMigration(from = 4, to = 5, spec = MigrationDeleteColumnLoginInItemTuBuy::class)
//    ]
////            MigrationDeleteTableUser
)
abstract class ItemToBuyDatabase: RoomDatabase() {

    abstract fun itemToBuyDao(): ItemToBuyDao
}