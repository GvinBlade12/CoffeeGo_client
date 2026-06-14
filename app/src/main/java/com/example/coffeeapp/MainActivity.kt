package com.example.coffeeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.coffeeapp.ui.theme.CoffeeAppTheme
import com.yandex.mapkit.MapKitFactory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import com.example.coffeeapp.ui.theme.currentAppTheme
import kotlinx.coroutines.launch
import com.example.coffeeapp.data.FavoritesRepository
import com.example.coffeeapp.managers.CartManager
import com.example.coffeeapp.managers.SessionManager
import com.example.coffeeapp.screens.CartScreen
import com.example.coffeeapp.screens.CoffeeDetailScreen
import com.example.coffeeapp.screens.FavoritesScreen
import com.example.coffeeapp.screens.LoginScreen
import com.example.coffeeapp.screens.MapScreen
import com.example.coffeeapp.screens.OrderHistoryScreen
import com.example.coffeeapp.screens.PaymentMethodsScreen
import com.example.coffeeapp.screens.ProfileScreen
import com.example.coffeeapp.screens.RegListScreen
import com.example.coffeeapp.screens.SupportScreen

@Serializable
data class CoffeeModel(
    val id: Int,
    val name: String,
    val type: String,
    val rating: Double,
    val imageUrl: String,
    val description: String
)

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("4d17a985-9565-45a6-92e4-ccecdabbef13")
        MapKitFactory.initialize(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Тема
        currentAppTheme.value = SessionManager.getAppTheme(this)

        setContent {
            CoffeeAppTheme {
                val globalNavController = rememberNavController()
                val context = androidx.compose.ui.platform.LocalContext.current
                val startScreen = if (SessionManager.isLoggedIn(context)) "catalog" else "login"

                NavHost(
                    navController = globalNavController,
                    startDestination = startScreen
                ) {
                    composable(route = "login") {
                        LoginScreen(navController = globalNavController)
                    }
                    composable(route = "registration") {
                        RegListScreen(navController = globalNavController)
                    }
                    composable(route = "catalog") {
                        MainScreenContainer(globalNavController = globalNavController)
                    }
                    composable(route = "cart") {
                        CartScreen(navController = globalNavController)
                    }
                    composable("order_history") {
                        OrderHistoryScreen(navController = globalNavController)
                    }
                    composable("payment_methods"){
                        PaymentMethodsScreen(navController = globalNavController)
                    }
                    composable("support") {
                        SupportScreen(navController = globalNavController)
                    }
                    composable("favorites") {
                        FavoritesScreen(navController = globalNavController, client = client)
                    }
                    composable(route = "coffee_detail/{coffeeId}") { backStackEntry ->
                        val coffeeId = backStackEntry.arguments?.getString("coffeeId")?.toIntOrNull()
                        if (coffeeId != null) {
                            CoffeeDetailScreen(
                                coffeeId = coffeeId,
                                navController = globalNavController
                            )
                        }
                    }
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        //Запускаем движок Яндекс.Карт при старте активити
        MapKitFactory.getInstance().onStart()
    }
    override fun onStop() {
        //Останавливаем движок, когда приложение уходит в фон
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}


sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object CatalogList : BottomNavItem("catalog_list", "Каталог", Icons.Default.List)
    object Map : BottomNavItem("map_screen", "Карта", Icons.Default.Place)
    object Profile : BottomNavItem("profile_screen", "Профиль", Icons.Default.Person)
}

@Composable
fun CoffeeCard(
    coffee: CoffeeModel,
    client: HttpClient,          //Передаем клиент
    currentUserId: Int,         //Передаем ID юзера
    isFavoriteInitial: Boolean,  //Было ли в избранном
    onCardClick: (Int) -> Unit
) {
    // Локальное состояние: закрашено сердечко прямо сейчас на экране или нет
    var isFavorite by remember(coffee.id) { mutableStateOf(isFavoriteInitial) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick(coffee.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF184E50)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFE7CC89))
            ) {
                AsyncImage(
                    model = coffee.imageUrl,
                    contentDescription = coffee.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // ОЖИВЛЯЕМ КНОПКУ СЕРДЕЧКА
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (isFavorite) {
                                // Если уже лайкнуто — удаляем
                                val success = FavoritesRepository.removeFavorite(client, currentUserId, coffee.id)
                                if (success) isFavorite = false
                            } else {
                                // Если не лайкнуто — добавляем
                                val success = FavoritesRepository.addFavorite(client, currentUserId, coffee.id)
                                if (success) isFavorite = true
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        // Если в избранном — показываем залитое сердечко, иначе контурное
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "В избранное",
                        // Делаем сердечко красным если оно выбрано
                        tint = if (isFavorite) Color(0xFFE57373) else Color.White
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "⭐ ${coffee.rating}",
                    fontSize = 12.sp,
                    color = Color(0xFFF8BD28),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = coffee.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = coffee.type,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "450 ₽",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4DD252)
                    )

                    Button(
                        onClick = {
                            CartManager.addCoffee(coffee, size = "M")
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
                    ) {
                        Text("+", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CoffeeListScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // получаем ID текущего пользователя
    val currentUserId = remember { SessionManager.getUserId(context) }

    var coffeeList by remember { mutableStateOf<List<CoffeeModel>>(emptyList()) }
    // сохранить ID, которые пользователь лайкнул
    var favoriteIds by remember { mutableStateOf<List<Int>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            //Загружаем весь кофе паралельно
            val coffeesResponse: List<CoffeeModel> = client.get("http://192.168.0.105:8080/coffees").body()
            coffeeList = coffeesResponse

            //Загружаем лайки этого пользователя с сервера
            val favoritesResponse = FavoritesRepository.getFavorites(client, currentUserId)
            favoriteIds = favoritesResponse

            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Неизвестная ошибка сети"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5AB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Coffee Catalog",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3E2723),
                modifier = Modifier.padding(start = 16.dp)
            )

            IconButton(
                onClick = { navController.navigate("cart") }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Корзина",
                    tint = Color(0xFF3E2723),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF3E2723))
            } else if (errorMessage != null) {
                Text(
                    text = "Ошибка загрузки: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(coffeeList) { coffee ->
                        // Проверяем содержит ли список избранного ID этого кофе
                        val isFavorite = favoriteIds.contains(coffee.id)

                        CoffeeCard(
                            coffee = coffee,
                            client = client,
                            currentUserId = currentUserId,
                            isFavoriteInitial = isFavorite, // передаем статус
                            onCardClick = { clickedId ->
                                navController.navigate("coffee_detail/$clickedId")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenContainer(globalNavController: NavController) {
    val localNavController = rememberNavController()
    val items = listOf(BottomNavItem.CatalogList, BottomNavItem.Map, BottomNavItem.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF3E2723),
                contentColor = Color.White
            ) {
                val navBackStackEntry by localNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title, fontSize = 12.sp) },
                        selected = currentRoute == item.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFF3E5AB),
                            selectedTextColor = Color(0xFFF3E5AB),
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = Color(0xFF5D4037)
                        ),
                        onClick = {
                            if (currentRoute != item.route) {
                                localNavController.navigate(item.route) {
                                    popUpTo(localNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = localNavController,
            startDestination = BottomNavItem.CatalogList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.CatalogList.route) {
                CoffeeListScreen(navController = globalNavController)
            }
            composable(BottomNavItem.Map.route) {
                MapScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(navController = globalNavController)
            }
        }
    }
}