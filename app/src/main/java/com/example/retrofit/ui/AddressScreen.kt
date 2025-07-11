package com.example.retrofit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AddressScreen(
    onContinue: (name: String, phone: String, address: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val filled = name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()
    Column(
        Modifier
            .fillMaxSize()
            .padding(22.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Delivery Address",
            fontWeight = FontWeight.Bold,
            fontSize = 23.sp,
            color = Color(0xFFFC7303)
        )
        Spacer(Modifier.height(34.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { if (it.length <= 10) phone = it.filter { c -> c.isDigit() } },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Full Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(30.dp))
        Button(
            onClick = { if (filled) onContinue(name, phone, address) },
            enabled = filled,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Proceed to Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
