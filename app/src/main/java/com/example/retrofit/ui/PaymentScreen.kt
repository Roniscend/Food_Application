package com.example.retrofit.ui

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.razorpay.Checkout
import org.json.JSONObject

@Composable
fun PaymentScreen(
    amount: Int, // INR
    userName: String,
    userPhone: String,
    userAddress: String,
    onPaymentSuccess: () -> Unit,
    onPaymentError: () -> Unit
) {
    val context = LocalContext.current
    var isPaying by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (isPaying) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFFFC7303))
                Spacer(Modifier.height(16.dp))
                Text("Processing payment...", fontSize = 18.sp)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Order Total", fontSize = 20.sp, color = Color.Gray)
                Text("₹$amount", fontSize = 38.sp, color = Color(0xFFFC7303))
                Spacer(modifier = Modifier.height(44.dp))
                Button(onClick = {
                    isPaying = true
                    launchRazorpay(
                        context,
                        amount,
                        userName,
                        userPhone,
                        onPaymentSuccess,
                        onPaymentError
                    ) {
                        isPaying = false
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(54.dp)) {
                    Text("Pay with Razorpay", fontSize = 18.sp)
                }
            }
        }
    }
}

fun launchRazorpay(
    context: Context,
    amount: Int,
    userName: String,
    userPhone: String,
    onSuccess: () -> Unit,
    onError: () -> Unit,
    onComplete: () -> Unit
) {
    val activity = context as? Activity ?: return
    val checkout = Checkout()
    checkout.setKeyID("rzp_test_obd9dK6mvBWAgQ") // Test key, replace in production!
    try {
        val options = JSONObject()
        options.put("name", userName)
        options.put("description", "Food Order")
        options.put("currency", "INR")
        options.put("amount", amount * 100) // Razorpay expects amount in paise
        options.put("prefill", JSONObject().apply {
            put("contact", userPhone)
        })
        options.put("theme", JSONObject().apply { put("color", "#FC7303") })
        checkout.open(activity, options)

        // You must hook into onPaymentSuccess and onPaymentError in Activity!!!
        Toast.makeText(context, "Complete payment and return here.", Toast.LENGTH_LONG).show()
        onSuccess() // For demo only. In a real app use Razorpay's callback in your Activity!
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to start payment: ${e.localizedMessage}", Toast.LENGTH_LONG)
            .show()
        onError()
    } finally {
        onComplete()
    }
}
