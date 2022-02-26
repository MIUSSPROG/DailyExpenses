package com.example.dailyexpenses.api

import com.google.gson.annotations.SerializedName

data class Child(
    val id: Int=0,
    val login: String,
    @SerializedName("parent")
    val parentId: Int
    )

//data class ChildPost(
//    val login: String,
//    @SerializedName("parent")
//    val parentId: String
//    )