package com.example.coffeeapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffeeapp.managers.SessionManager
import com.example.coffeeapp.data.FavoritesRepository
import io.ktor.client.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, client: HttpClient) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Достаем ID текущего залогиненного пользователя
    val currentUserId = remember { SessionManager.getUserId(context) }

    // Храним список ID избранных товаров
    var favoriteIds by remember { mutableStateOf<List<Int>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        favoriteIds = FavoritesRepository.getFavorites(client, currentUserId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF3E2723))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3E5AB))
            )
        },
        containerColor = Color(0xFFF3E5AB)
    ) { innerPadding ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3E2723))
            }
        } else if (favoriteIds.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("В избранном пока ничего нет ☕", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            // Выводим сетку из лайкнутых товаров
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteIds) { id ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Кофе №$id", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Кнопка быстрого удаления
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    val success = FavoritesRepository.removeFavorite(client, currentUserId, id)
                                    if (success) {
                                        favoriteIds = favoriteIds.filter { it != id }
                                    }
                                }
                            }) {
                                Text("Удалить", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}