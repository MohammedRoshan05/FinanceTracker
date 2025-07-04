package com.example.financemanager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Transactions(
    @SerialName("user_id") val userId: String,
    @SerialName("amount") val amount: Int,
    @Serializable(with = LocalDateSlashSerializer::class)
    val date: LocalDate,
    @SerialName("type") val type: String
)
