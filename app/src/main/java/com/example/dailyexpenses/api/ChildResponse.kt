package com.example.dailyexpenses.api

import com.google.gson.annotations.SerializedName

data class Child(
    val id: Int=0,
    val login: String = "",
    val password: String = "",
    val confirmed: Boolean = false,
    @SerializedName("parent")
    val parentId: Int? = null
    )

data class ChildInvitation(val parent: Int, val confirmed: Boolean)

data class ChildOfParent(
    @SerializedName("child_id")
    val id: Int,
    @SerializedName("child_login")
    val login: String,
    val confirm: Boolean
)

//data class ChildPost(
//    val login: String,
//    @SerializedName("parent")
//    val parentId: String
//    )