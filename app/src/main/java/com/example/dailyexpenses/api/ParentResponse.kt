package com.example.dailyexpenses.api

import com.google.gson.annotations.SerializedName

data class Parent(
    val id: Int=0,
    val login: String,
    val password: String
    ){
    override fun toString(): String {
        return login
    }
}

data class ParentChildren(
    val login: String,
    val children: List<Child>
)

//data class ParentPost(
//    val login: String,
//    val password: String
//    )