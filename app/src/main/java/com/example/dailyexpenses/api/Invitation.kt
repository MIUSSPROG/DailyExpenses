package com.example.dailyexpenses.api

data class Invitation(val id: Int = 0, val child: Int, val parent: Int, val confirm: Boolean)