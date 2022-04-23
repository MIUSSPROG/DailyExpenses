package com.example.dailyexpenses.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class Plan(
    val name: String,
    val price: Float,
    val date: Long,
    val confirm: Boolean?,
    @SerializedName("category")
    val categoryId: Int,
    @SerializedName("child")
    val childId: Int,
    val image: MultipartBody.Part? = null
)

data class ChildrenPlan(
    val login: String,
    val plans: List<Plan>
)