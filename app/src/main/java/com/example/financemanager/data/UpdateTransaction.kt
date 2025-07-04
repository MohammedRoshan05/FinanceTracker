package com.example.financemanager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UpdateTransaction(
    @SerialName("email") val email: String,
    @SerialName("amount") val amount: Int,
    @Serializable(with = LocalDateSlashSerializer::class)
    val date: LocalDate,
    @SerialName("type") val type: String
)
