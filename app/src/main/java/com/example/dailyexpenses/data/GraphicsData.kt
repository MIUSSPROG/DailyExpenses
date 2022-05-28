package com.example.dailyexpenses.data

data class HistogramData(
    val date: Long,
    val sumPrice: Float
)

data class DiagramData(
    val category: String,
    val sumPrice: Float
)


