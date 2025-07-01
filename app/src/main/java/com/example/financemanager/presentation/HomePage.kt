package com.example.financemanager.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.financemanager.R

@Composable
fun HomePage(name: String) {
    Box(modifier = Modifier.fillMaxSize().background(color = Color.White).padding(25.dp), contentAlignment = Alignment.Center){
        Column {
            Greeting(name)
            CreditCard()
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))
            TransactionList(modifier = Modifier)
        }
    }
}

@Composable
fun Greeting(name:String){
    Row(modifier = Modifier.fillMaxWidth().padding(20.dp).
    clip(RoundedCornerShape(16.dp)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically){
        Column {
            Text(text = "Good Morning",  style = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFFfca311),
            ))
            Text(text = "$name",  style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            ))
        }
        Icon(Icons.Default.Notifications, contentDescription = "")
    }
}

@Composable
fun CreditCard(){
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f).clip(RoundedCornerShape(36.dp))
        .background(color = Color.Black)
        .padding(horizontal = 35.dp, vertical = 16.dp)
         ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "1235 $",  style = TextStyle(
                fontSize = 27.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ))
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Text(text = "Balance",  style = TextStyle(
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            ))
            Spacer(modifier = Modifier.padding(15.dp).fillMaxWidth(0.3f).fillMaxHeight(0.2f)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color.Red))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = "1234 5678 1234 3245",  style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                ))
                Image(painter = painterResource(id = R.drawable.credit_card), contentDescription = "")
            }
        }
    }
}

@Composable
fun TransactionList(modifier: Modifier){
    Box(modifier = modifier.fillMaxWidth().fillMaxHeight().clip(RoundedCornerShape(36.dp))
        .padding(horizontal = 35.dp, vertical = 16.dp)){
        Column(){
            Text(text = "Recent Transactions",  style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            ))
            LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 20.dp)) {
                items(10) {
                    TransactionItem()
                }
            }
        }
    }
}

@Composable
fun TransactionItem(){
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .padding(vertical = 16.dp),){
        Icon(Icons.Default.ShoppingCart, contentDescription = "")
        Column(){
            Text(text = "Item",  style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            ))
            Spacer(Modifier.height(10.dp))
            Text(text = "Date",  style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Thin,
            ))
        }
        Spacer(modifier = Modifier.fillMaxWidth(0.7f))
        Text(text = "$3214",  style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        ))

    }
}