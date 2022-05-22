package com.example.dailyexpenses.migrations

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec
import com.example.dailyexpenses.data.ItemToBuy

@DeleteTable(tableName = "User")
class MigrationDeleteTableUser: AutoMigrationSpec

@DeleteColumn(columnName = "login", tableName = "ItemToBuy")
class MigrationDeleteColumnLoginInItemTuBuy: AutoMigrationSpec


