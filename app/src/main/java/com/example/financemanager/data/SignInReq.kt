package com.example.financemanager.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SignInReq(
    @SerialName("email") val userEmail: String,
    @SerialName("password") val userPassword: String,
)