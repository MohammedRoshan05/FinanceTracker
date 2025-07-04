package com.example.financemanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financemanager.data.MyViewModel
import java.time.LocalDate

@Composable
fun Transaction(
    viewModel: MyViewModel,
){

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(25.dp), contentAlignment = Alignment.Center){
        Column {
            val modifier = Modifier.weight(1f)
            Header(modifier = modifier)
            DateSelect(modifier = modifier)
            MoneySelect(modifier = modifier)
            AmountSelect(modifier = modifier)
            PaymentType(modifier = Modifier.weight(4f))

        }
    }
}

@Composable
private fun Header(modifier: Modifier){
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(20.dp)
        .clip(RoundedCornerShape(16.dp)),
//        .background(color = Color.Yellow)
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically){
        Icon(Icons.Default.ArrowBack, contentDescription = "")
        Spacer(modifier = Modifier.weight(1f))
        Text(modifier = Modifier.weight(2f),
            text = "Add Expense",  style = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        )
    }
}

@Composable
fun DateSelect(modifier: Modifier){
    Row(modifier = modifier
        .fillMaxWidth()
        .background(color = Color(0xFFb6daac)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.DateRange,
            contentDescription = "",
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = "${LocalDate.now()}",
            modifier = Modifier.weight(2f),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
fun MoneySelect(modifier: Modifier){
    Row(modifier = modifier
        .fillMaxWidth()
        .background(color = Color(0xFFf9f9fa)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Add, contentDescription = "",modifier = Modifier.weight(1f))
        Text(modifier = Modifier.weight(1f),
            text = "$1243",
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "",modifier = Modifier.weight(1f))
    }
}

@Composable
fun AmountSelect(modifier: Modifier){
    Row(modifier = modifier
        .fillMaxWidth()
        .background(color = Color(0xFFf9f9fa)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "",modifier = Modifier.weight(1f))
        Text(modifier = Modifier.weight(1f),
            text = "$1243",
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun PaymentType(modifier: Modifier){
    Column(modifier = modifier
        .fillMaxWidth()
        .background(color = Color(0xFFf9f9fa))){
        Text(
            modifier = Modifier.weight(0.5f),
            text = "Payment Type",
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        RadioButtonSingleSelection(modifier = Modifier.weight(3f))
    }
}

@Composable
fun RadioButtonSingleSelection(modifier: Modifier = Modifier) {
    val radioOptions = listOf("Cash", "Card", "UPI")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    Column(modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = androidx.compose.ui.semantics.Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}