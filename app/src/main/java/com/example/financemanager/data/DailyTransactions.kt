package com.example.financemanager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate


@Serializable
data class DailyTransactions(
    @SerialName("total") val total: Int,
    @Serializable(with = LocalDateSlashSerializer::class)
    val date: LocalDate,
)