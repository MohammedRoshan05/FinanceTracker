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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.financemanager.data.MyViewModel
import com.example.financemanager.data.TransactionType
import kotlinx.coroutines.launch

@Composable
fun Transaction(
    viewModel: MyViewModel,
){
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(25.dp), contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val modifier = Modifier.weight(1f)
            Header(modifier = modifier)
            DatePickerDocked(viewModel)
            TypeSelect(modifier,viewModel)
            AmountSelect(modifier,viewModel)
            PaymentType(Modifier.weight(4f),viewModel)
            Button(
                onClick = {
                    scope.launch {
                        viewModel.createTransaction()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RectangleShape
            ) {
                Text("Create")
            }
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
fun TypeSelect(
    modifier: Modifier,
    viewModel: MyViewModel
){
    Column(modifier = modifier
        .fillMaxWidth()
        .background(color = Color(0xFFf9f9fa)),
        verticalArrangement = Arrangement.Center) {
        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically){
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.weight(1f),
                text = viewModel.type,
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {viewModel.isTypeDropDown = true}, modifier = Modifier.weight(1f)){
                Icon(Icons.Default.Add, contentDescription = "")
            }
        }
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = viewModel.isTypeDropDown,
            onDismissRequest = {viewModel.isTypeDropDown = false}
        ) {
            TypeDropDownItems(viewModel)
        }
    }
}

@Composable
fun TypeDropDownItems(viewModel: MyViewModel){
    for(i in TransactionType.entries){
        DropdownMenuItem(
            text = { Text(i.toString()) },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            onClick = {
                viewModel.type = i.toString()
                viewModel.isTypeDropDown = false
            },
        )
    }
}

@Composable
fun AmountSelect(
    modifier: Modifier,
    viewModel: MyViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // Local text state, initialized from viewModel.amount
    var text by remember { mutableStateOf(viewModel.amount.toString()) }
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color(0xFFf9f9fa)),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = if(viewModel.ExpenseOrIncome) Color.Red else Color.Green,
            ),
            onClick = { viewModel.ExpenseOrIncome = !viewModel.ExpenseOrIncome },
            shape = RectangleShape,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if(viewModel.ExpenseOrIncome) "Expense" else "Income",
            )
        }
        Spacer(modifier = Modifier.width(25.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                // 1) filter to digits only
                val filtered = input.filter { it.isDigit() }

                // 2) update our local text state
                text = filtered

                // 3) safely parse it (or default to 0)
                viewModel.amount = filtered.toIntOrNull() ?: 0
            },
            label = { Text("Amount") },
            modifier = Modifier
                .weight(3f)               // use weight instead of fillMaxWidth()
                .background(color = Color(0xFFf9f9fa)),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done   // show "Done" on the keyboard
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
        )
    }
}


@Composable
fun PaymentType(
    modifier: Modifier,
    viewModel: MyViewModel
){
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
        RadioButtonSingleSelection(modifier = Modifier.weight(3f), viewModel = viewModel)
    }
}

@Composable
fun RadioButtonSingleSelection(
    viewModel: MyViewModel,
    modifier: Modifier = Modifier
) {
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
                    onClick = {viewModel.paymentOption = selectedOption}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDocked(
    viewModel: MyViewModel
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        viewModel.convertMillisToDate(it)
    } ?: ""

    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (datePickerState.selectedDateMillis != null) {
            viewModel.date = selectedDate
            viewModel.showDatePicker = false
        }
    }


    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("DOB") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    println("Pressed")
                    viewModel.showDatePicker = !viewModel.showDatePicker
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth().height(64.dp)

        )
        if (viewModel.showDatePicker) {
            Popup(
                onDismissRequest = { viewModel.showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = true,
                        modifier = Modifier.height(250.dp)
                    )
                }
            }
        }
    }
}
