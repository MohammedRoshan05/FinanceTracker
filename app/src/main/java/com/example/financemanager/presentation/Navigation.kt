package com.example.financemanager.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financemanager.data.MyViewModel


sealed class Screen(val route: String) {
    object HomePage: Screen("home_page")
    object Statistics : Screen("stats")
    object Transaction : Screen("transaction")
}

@Composable
fun Navigation(
    navController : NavHostController,
    myViewModel: MyViewModel,
){
    NavHost(navController = navController, startDestination = Screen.HomePage.route){
        composable(route = Screen.HomePage.route) {
            HomePage(name = "Roshan", viewModel = myViewModel)
        }
        composable(route = Screen.Statistics.route) {
            Statistics(viewModel = myViewModel)
        }
        composable(route = Screen.Transaction.route) {
            Transaction(viewModel = myViewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController){
    val items = listOf(
        BottomBarNavigationItem(
            title = "home_page",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
        ),
        BottomBarNavigationItem(
            title = "stats",
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
        ),
        BottomBarNavigationItem(
            title = "transaction",
            selectedIcon = Icons.Filled.Create,
            unselectedIcon = Icons.Outlined.Create,
        )
    )
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    NavigationBar {
        items.forEachIndexed{index,item->
            NavigationBarItem(
                selected = index == selectedItem,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.title)
                },
                icon = {
                    Icon(if(index == selectedItem) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title)
                },
            )
        }
    }
}

data class BottomBarNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)