package com.example.financemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.financemanager.presentation.HomePage
import com.example.financemanager.presentation.Statistics
import com.example.financemanager.presentation.Transaction
import com.example.financemanager.ui.theme.FinanceManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceManagerTheme {
//                HomePage("Roshan")
//                Statistics()
                Transaction()
            }
        }
    }
}