package com.example.retrofit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

// ---------- DATA MODELS ----------
@Serializable
data class Meal(
    @SerialName("idMeal") val id: String,
    @SerialName("strMeal") val name: String,
    @SerialName("strMealThumb") val thumbnail: String,
    // Deterministic price by id for consistency
    val priceUsd: Int = 5 + (id.filter { it.isDigit() }.toBigIntegerOrNull()?.toInt() ?: 0) % 16
) {
    val priceInr: Int get() = priceUsd * 85
}

@Serializable
data class MealsResponse(
    val meals: List<Meal> = emptyList()
)

// ---------- RETROFIT SERVICE ----------
interface MealDbService {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealsResponse

    @GET("lookup.php")
    suspend fun lookupMeal(@Query("i") id: String): MealsResponse
}

// ---------- RETROFIT INSTANCE PROVIDER ----------
object RetrofitProvider {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val mealService: MealDbService by lazy { retrofit.create(MealDbService::class.java) }
}

// ---------- REPOSITORY ----------
class MealRepository(private val api: MealDbService = RetrofitProvider.mealService) {
    suspend fun getMeals(query: String): List<Meal> {
        return api.searchMeals(query).meals
    }
    suspend fun getMeal(id: String): Meal? {
        return api.lookupMeal(id).meals.firstOrNull()
    }
}

// ---------- VIEWMODEL ----------
class MealViewModel(private val repository: MealRepository = MealRepository()) : ViewModel() {

    // Holds the complete list of all meals (for HomeScreen)
    private val _allMeals = MutableStateFlow<List<Meal>>(emptyList())
    val allMeals: StateFlow<List<Meal>> = _allMeals

    // Holds the search results (for SearchScreen)
    private val _searchMeals = MutableStateFlow<List<Meal>>(emptyList())
    val searchMeals: StateFlow<List<Meal>> = _searchMeals

    init {
        loadAllMeals()
    }

    // Loads all meals (call with "" for homepage)
    fun loadAllMeals(query: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val meals = kotlin.runCatching { repository.getMeals(query) }.getOrDefault(emptyList())
            _allMeals.value = meals
        }
    }

    // Loads filtered meals (for search screen)
    fun searchMeals(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val meals = kotlin.runCatching { repository.getMeals(query) }.getOrDefault(emptyList())
            _searchMeals.value = meals
        }
    }

    // Call this when leaving search to clear search results
    fun clearSearchMeals() {
        _searchMeals.value = emptyList()
    }
}
