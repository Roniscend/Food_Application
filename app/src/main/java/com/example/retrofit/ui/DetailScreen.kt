package com.example.retrofit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.retrofit.Meal

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    meal: Meal?,
    onBack: () -> Unit = {},
    onAddToCart: () -> Unit = {}
) {
    if (meal == null) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddToCart,
                icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Add to Cart") },
                text = { Text("Add to Cart") },
                containerColor = Color(0xFFFC7303),
                contentColor = Color.White
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(meal.name, maxLines = 1, fontSize = 21.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFFFDEFF3))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                meal.name,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "₹${meal.priceInr}",
                color = Color(0xFFFC7303),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                "Ingredients",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            IngredientsList(meal = meal)
        }
    }
}

@Composable
fun IngredientsList(meal: Meal) {
    // As MealDB ingredients are in strIngredient1..20 etc, you may want to map them. For now demo:
    // Use real parsing if expanded model!
    Text(
        "Ingredient list here...",
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}
