package com.example.retrofit.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.retrofit.Meal
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwiggyHomeScreen(
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit,
    categories: List<String> = listOf(
        "Indian",
        "Dessert",
        "Chinese",
        "Italian",
        "Seafood",
        "Vegan",
        "Drink"
    ),
    onCategoryClick: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onLogoClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 12.dp, start = 10.dp, end = 10.dp, bottom = 0.dp)
                .fillMaxWidth()
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = com.example.retrofit.R.drawable.ic_launcher_foreground), // replace with app logo
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clickable { onLogoClick() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Food App", // Or "Swiggy Clone"
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFFFC7303)
            )
        }
        BannerCarousel(meals)
        SearchBar(onSearch)
        CategoryChips(categories, onCategoryClick)
        MealsGrid(meals, onMealClick)
    }
}

@Composable
fun BannerCarousel(meals: List<Meal>) {
    val images = meals.take(4).map { it.thumbnail }
    var current by remember { mutableStateOf(0) }

    LaunchedEffect(images.size) {
        if (current >= images.size) current = 0
        if (images.size > 1) {
            while (true) {
                delay(2500L)
                current = (current + 1) % images.size
            }
        }
    }

    if (images.isNotEmpty()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFFDEFF3))
        ) {
            AsyncImage(
                model = images[current.coerceIn(0, images.lastIndex)],
                contentDescription = "Banner Meal",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            )
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                images.forEachIndexed { ix, _ ->
                    Box(
                        Modifier
                            .padding(horizontal = 2.dp)
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (ix == current) Color(0xFFFC7303) else Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChips(categories: List<String>, onCategoryClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        for (cat in categories) {
            FilterChip(cat, onClick = { onCategoryClick(cat) })
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun FilterChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier
            .clickable { onClick() }) {
        Box(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFC7303)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealsGrid(meals: List<Meal>, onMealClick: (Meal) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(meals) { meal ->
            SwiggyMealCard(meal = meal, onMealClick = onMealClick)
        }
    }
}

@Composable
fun SwiggyMealCard(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onMealClick(meal) }
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                meal.name,
                Modifier.padding(horizontal = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "₹${meal.priceInr}",
                color = Color(0xFFFC7303),
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    Surface(
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        TextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            },
            trailingIcon = {
                Row {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            onSearch(query)
                        }) {
                            Icon(Icons.Filled.ArrowForward, contentDescription = "Go")
                        }
                        IconButton(onClick = {
                            query = ""
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                }
            },
            placeholder = {
                Text(
                    text = "Search for delicious food…",
                    textAlign = TextAlign.Start,
                    color = Color(0xFF9E9E9E)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch(query) }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF2F2F2),
                unfocusedContainerColor = Color(0xFFF2F2F2),
                disabledContainerColor = Color(0xFFF2F2F2),
                errorContainerColor = Color(0xFFF2F2F2),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
