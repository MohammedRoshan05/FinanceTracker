package com.example.financemanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
import com.example.financemanager.data.MyViewModel


@Composable
fun Statistics(
    viewModel: MyViewModel,
){
    Box(modifier = Modifier.fillMaxSize().background(color = Color.White).padding(25.dp), contentAlignment = Alignment.Center){
        Column(modifier = Modifier.fillMaxSize()){
            Header(modifier = Modifier.weight(1f))
            Graph(modifier = Modifier.weight(3f).background(color = Color.Blue))
            TransactionList(modifier = Modifier.weight(4f),viewModel.todayTransactions)
        }
    }
}

@Composable
private fun Header(modifier: Modifier){
        Row(modifier = modifier.fillMaxWidth().
        padding(20.dp).
        clip(RoundedCornerShape(16.dp)).background(color = Color.Yellow),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            Icon(Icons.Default.ArrowBack, contentDescription = "")
            Text(text = "Statistics",  style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            ))
            Icon(Icons.Default.DateRange, contentDescription = "")
        }
        Text(
            text = "$1243",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        Text(
            text = "Date",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
            )
        )
}

@Composable
fun Graph(modifier: Modifier){
    JetpackComposeBasicLineChart(modifier = modifier)
}