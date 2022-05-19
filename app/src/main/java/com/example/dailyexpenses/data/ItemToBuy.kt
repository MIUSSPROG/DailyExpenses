package com.example.dailyexpenses.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.dailyexpenses.api.Plan
import com.example.dailyexpenses.utils.HelperMethods
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["login"],
            childColumns = ["login"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class ItemToBuy(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val categoryId: Int,
    val category: String,
    val price: Float,
    val date: Long,
    val confirm: Boolean?,
    val send: Boolean = false,
    val remoteDbId: Int? = null,
    val login: String? = null
) {
    fun convertToDbPlan(childId: Int)  =
        Plan(
            id = id,
            name = name,
            price = price,
            date = date,
            confirm = confirm,
            categoryId = categoryId,
            childId = childId
        )
}
