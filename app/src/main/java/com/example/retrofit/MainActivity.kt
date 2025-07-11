package com.example.retrofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.retrofit.ui.theme.RetrofitTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.retrofit.ui.SwiggyHomeScreen
import com.example.retrofit.ui.DetailScreen
import com.example.retrofit.ui.AddressScreen
import com.example.retrofit.ui.PaymentScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetrofitTheme {
                val mealViewModel: MealViewModel = viewModel()
                val allMeals by mealViewModel.allMeals.collectAsStateWithLifecycle()
                val searchMeals by mealViewModel.searchMeals.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                var selectedMeal: Meal? = null
                var userName = ""
                var userPhone = ""
                var userAddress = ""

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        mealViewModel.loadAllMeals("")
                        SwiggyHomeScreen(
                            meals = allMeals,
                            onMealClick = { meal ->
                                selectedMeal = meal
                                navController.navigate("detail")
                            },
                            onSearch = { query ->
                                if (query.isNotBlank()) {
                                    mealViewModel.searchMeals(query)
                                    navController.navigate("search/$query")
                                }
                            },
                            onLogoClick = {
                                mealViewModel.loadAllMeals("")
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable("detail") {
                        DetailScreen(
                            meal = selectedMeal,
                            onBack = { navController.popBackStack() },
                            onAddToCart = {
                                navController.navigate("address")
                            }
                        )
                    }
                    composable("address") {
                        AddressScreen { name, phone, address ->
                            userName = name
                            userPhone = phone
                            userAddress = address
                            navController.navigate("payment")
                        }
                    }
                    composable("payment") {
                        PaymentScreen(
                            amount = selectedMeal?.priceInr ?: 0,
                            userName = userName,
                            userPhone = userPhone,
                            userAddress = userAddress,
                            onPaymentSuccess = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onPaymentError = {
                                navController.popBackStack("address", false)
                            }
                        )
                    }
                    composable("search/{q}") { backStackEntry ->
                        val q = backStackEntry.arguments?.getString("q") ?: ""
                        SearchResultScreen(query = q, meals = searchMeals, onMealClick = { meal ->
                            selectedMeal = meal
                            navController.navigate("detail")
                        }, onBack = {
                            mealViewModel.loadAllMeals("")
                            mealViewModel.clearSearchMeals()
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun MealsList(meals: List<Meal>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(meals) { meal ->
            MealItem(meal)
        }
    }
}

@Composable
fun MealItem(meal: Meal) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = meal.thumbnail,
                contentDescription = meal.name,
                modifier = Modifier.size(80.dp)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = meal.name)
                Text(text = "₹${meal.priceInr}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    query: String,
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Results for \"$query\"") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        if (meals.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFC7303))
            }
        } else {
            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                MealsList(meals = meals, modifier = Modifier.padding(padding))
            }
        }
    }
}