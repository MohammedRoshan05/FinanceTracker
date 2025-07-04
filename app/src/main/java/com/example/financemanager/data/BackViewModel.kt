package com.example.financemanager.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.financemanager.presentation.Screen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.time.measureTime

class MyViewModel : ViewModel() {
    companion object {
        private const val API_HOST = "localhost"
        private const val API_PORT = 8000
    }

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true   // optional
                prettyPrint = true         // optional
            })
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
                host = API_HOST
                port = API_PORT
            }
        }
    }

    var signedIn by mutableStateOf(false)
    var email by mutableStateOf("Roshan")
    var password by mutableStateOf("1234")
    var confPasss by mutableStateOf("1234")
    var token by mutableStateOf("")

    var updateTransaction by mutableStateOf<UpdateTransaction>(
        UpdateTransaction(
        "",0, LocalDate.now(),"",
        )
    )
    suspend fun SignUp() : String{
        if(password != confPasss){
            return ("Passwords do not match")
        }
        val signUpreq = SignUpReq(email, password)
        println("req body")
        println(signUpreq)
        val responseBody = client.post("/create"){
            contentType(ContentType.Application.Json)
            setBody(signUpreq)
        }
        if(responseBody.status.value in 200..300){
            email = ""
            password = ""
            confPasss = ""
            return ""
        }
        return responseBody.bodyAsText()
    }
    suspend fun SignIn():String{
        val signInReq = SignInReq(email, password)
        println(email)
        println(password)
        println(signInReq)
        val responseBody = client.post("/login"){
            contentType(ContentType.Application.Json)
            setBody(signInReq)
        }
        if(responseBody.status.value == 200){
            token = responseBody.headers[HttpHeaders.Authorization].toString()
            println("token string $token")
            getDailyBalance()
            getDailyTransactions()
            getTransactions()
            signedIn = true
            return ""
        }else{
            return responseBody.bodyAsText()
        }
    }

    var dailyBalance by mutableStateOf<List<DailyBalance>>(emptyList())
    var todayBalance by mutableStateOf(0)
    suspend fun getDailyBalance() {
        val response = client.get("/balance/$email") {
            header(
                HttpHeaders.Authorization,
                "$token"
            )
        }
        if(response.status.value in 200..299){
            dailyBalance = response.body<List<DailyBalance>>()
            todayBalance = dailyBalance[dailyBalance.size - 1].balance
        }
    }

    var dailyTransaction by mutableStateOf<List<DailyTransactions>>(emptyList())
    suspend fun getDailyTransactions(){
        val response = client.get("/transactions/$email") {
            header(
                HttpHeaders.Authorization,
                "$token"
            )
        }
        if (response.status.value in 200..299) {
            dailyTransaction = response.body<List<DailyTransactions>>()
            println(dailyTransaction)
        }
    }

    var allTransactions by mutableStateOf<List<Transactions>>(emptyList())
    var todayTransactions by mutableStateOf<List<Transactions>>(emptyList())
    suspend fun getTransactions(){
        val response = client.get("/transaction/$email") {
            header(
                HttpHeaders.Authorization,
                "$token"
            )
        }
        if (response.status.value in 200..299) {
            todayTransactions = response.body<List<Transactions>>()
            todayTransactions = todayTransactions.filter { it.date == LocalDate.now().minusDays(2) }
            println(todayTransactions)
        }
    }
}

