package com.example.dailyexpenses.api

import com.google.gson.annotations.SerializedName

data class Child(
    val id: Int=0,
    val login: String = "",
    val password: String = "",
    val confirmed: Boolean = false,
    @SerializedName("parent")
    val parentId: Int? = null
    ){
    override fun toString(): String {
        return login
    }
}

data class ChildInvitation(
    val parent: Int?,
    val confirmed: Boolean
    )

data class ChildParent(
    @SerializedName("parent_id")
    val parentId: Int,
    @SerializedName("parent_login")
    val parentLogin: String,
    val confirmed: Boolean
    )

//data class ChildPost(
//    val login: String,
//    @SerializedName("parent")
//    val parentId: String
//    )