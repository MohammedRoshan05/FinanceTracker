package com.example.financemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financemanager.data.MyViewModel
import com.example.financemanager.presentation.BottomNavigationBar
import com.example.financemanager.presentation.HomePage
import com.example.financemanager.presentation.Navigation
import com.example.financemanager.presentation.SignInScreen
import com.example.financemanager.presentation.SignUpScreen
import com.example.financemanager.presentation.Statistics
import com.example.financemanager.presentation.Transaction
import com.example.financemanager.ui.theme.FinanceManagerTheme

class MainActivity : ComponentActivity() {
    var myViewModel: MyViewModel = MyViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceManagerTheme{
                var navcontroller = rememberNavController()
                if(!myViewModel.signedIn){
                    NavHost(navController = navcontroller, startDestination = "signup_page"){
                        composable(route = "signup_page"){
                            SignUpScreen(viewModel = myViewModel, navController = navcontroller)
                        }
                        composable(route = "signin_page"){
                              SignInScreen(viewModel = myViewModel, navController = navcontroller)
                        }
                    }
                }
                else{
                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(navcontroller)
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            Navigation(navcontroller,myViewModel)
                        }
                    }
                }
            }
        }
    }
}
