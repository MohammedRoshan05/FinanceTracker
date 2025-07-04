package com.example.financemanager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.financemanager.data.MyViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: MyViewModel
) {
    var scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.White).padding(25.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.weight(1f).align(alignment = Alignment.Start),
                text = "Sign Up",
                style = TextStyle(
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = "Email",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = {it ->
                        viewModel.email =  it
                    },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Password",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                OutlinedTextField(
                    value = viewModel.password,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {it ->
                        viewModel.password =  it
                    },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Confirm Password",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                OutlinedTextField(
                    value = viewModel.confPasss,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {it ->
                        viewModel.confPasss =  it
                    },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
            Button(onClick = {
                scope.launch {
                    val e = viewModel.SignUp()
                    if(e.equals("")) navController.navigate("signin_page")
                    else println(e)
                }
            },
                modifier = Modifier.weight(1f).fillMaxWidth().padding(20.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF010100)
                )
                ) {
                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))

        }
    }
}
