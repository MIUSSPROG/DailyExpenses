package com.example.dailyexpenses.api

data class Parent(
    val id: Int=0,
    val login: String,
    val password: String
    )

data class ParentChildren(
    val login: String,
    val children: List<Child>
)

//data class ParentPost(
//    val login: String,
//    val password: String
//    )