package com.example.dailyexpenses.api

import com.google.gson.annotations.SerializedName

data class Child(
    val id: Int=0,
    val login: String,
    val password: String,
    @SerializedName("parent")
    val parentId: Int? = null
    )

//data class ChildPost(
//    val login: String,
//    @SerializedName("parent")
//    val parentId: String
//    )