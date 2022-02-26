package com.example.dailyexpenses.api

import com.google.gson.annotations.SerializedName

data class Plan(
    val id: Int,
    val name: String,
    val price: Float,
    val date: String,
    val confirm: Boolean,
    @SerializedName("category")
    val categoryId: Int,
    @SerializedName("child")
    val childId: Int
)

data class ChildrenPlan(
    val login: String,
    val plans: List<Plan>
)