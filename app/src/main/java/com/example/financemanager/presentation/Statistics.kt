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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanager.data.MyViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


@Composable
fun Statistics(
    viewModel: MyViewModel,
){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(25.dp), contentAlignment = Alignment.Center){
        Column(Modifier.fillMaxSize()){
            Header(Modifier.weight(1f),viewModel)
            Graph(Modifier.weight(3f),viewModel)
            TransactionList(Modifier.weight(4f),viewModel.gTransactions)
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier,
    viewModel: MyViewModel
){
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(20.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(color = Color.Yellow),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically){
        Text(text = "Statistics",  style = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        ))
        IconButton(onClick = {viewModel.gDateSelect = true}) {
            Icon(Icons.Default.DateRange, contentDescription = "")
        }
    }
    if(viewModel.gDateSelect) {
        DateSelect(viewModel)
    }
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(20.dp)
        .background(color = Color.Yellow),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        Text(
            text = (viewModel.gBalance?.balance ?: 0).toString(),
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        Text(
            text = viewModel.gDate,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Light,
            )
        )
    }

}

@Composable
fun Graph(modifier: Modifier,viewModel: MyViewModel){
    JetpackComposeBasicLineChart(modifier = modifier, viewModel = viewModel )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelect(
    viewModel: MyViewModel,
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val scope = rememberCoroutineScope()
    DatePickerDialog(
        onDismissRequest = { viewModel.gDateSelect = false },
        confirmButton = {
            TextButton(onClick = {
                viewModel.gDate = datePickerState.selectedDateMillis?.let {
                    viewModel.convertMillisToDate(it)
                } ?: ""
                println("Date formatting happening")
                println(viewModel.gDate)
                scope.launch {
                    viewModel.getGTransactions()
//                    viewModel.getGDailyBalance()
//                    println("Dates changing?")
//                    println(viewModel.gDate)
                }
                viewModel.gDateSelect = false
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.gDateSelect = false }) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}