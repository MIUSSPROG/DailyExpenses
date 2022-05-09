package com.example.dailyexpenses.data

data class HistogramData(
    val date: Long,
    val sumPrice: Float
)

data class DiagramData(
    val categoryId: Int,
    val sumPrice: Float
)


