package com.smartstyle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartstyle.ui.viewmodel.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(vm: MainViewModel = hiltViewModel()){
    var input by remember { mutableStateOf("") }
    val reply by vm.chat.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)){
        Text("SmartStyle", style=MaterialTheme.typography.h5)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Ask AI") })
        Spacer(Modifier.height(8.dp))
        Button(onClick = { vm.askAssistant(input) }){ Text("Send") }
        Spacer(Modifier.height(16.dp))
        Text(reply ?: "")
    }
}
