package com.example.dailyexpenses.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemToBuy(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val price: Float,
    val date: String
)