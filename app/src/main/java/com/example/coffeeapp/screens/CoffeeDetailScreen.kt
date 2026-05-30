package com.example.coffeeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.coffeeapp.managers.CartManager
import com.example.coffeeapp.CoffeeModel
import com.example.coffeeapp.client
import io.ktor.client.call.body
import io.ktor.client.request.get

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeDetailScreen(coffeeId: Int, navController: NavController) {
    var coffee by remember { mutableStateOf<CoffeeModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedSize by remember { mutableStateOf("M") }

    LaunchedEffect(coffeeId) {
        try {
            // Поменяли IP на твой реальный, чтобы работало и на физическом смартфоне
            val response: List<CoffeeModel> = client.get("http://192.168.0.101:8080/coffees").body()
            coffee = response.find { it.id == coffeeId }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE7CC89))
            )
        },
        containerColor = Color(0xFFF3E5AB)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3E2723))
            }
        } else if (coffee != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = coffee!!.imageUrl,
                    contentDescription = coffee!!.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = coffee!!.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF3E2723))
                        Text(text = coffee!!.type, fontSize = 16.sp, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Рейтинг", tint = Color(0xFFFFB300))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = coffee!!.rating.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Заменили устаревший Divider на современный HorizontalDivider
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Описание", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = coffee!!.description, fontSize = 15.sp, color = Color.DarkGray, lineHeight = 22.sp)

                Spacer(modifier = Modifier.weight(1f))

                Text(text = "Размер стаканчика", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("S", "M", "L").forEach { size ->
                        val isSelected = selectedSize == size
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF3E2723) else Color(0xFFF5F5F5))
                                .clickable { selectedSize = size },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = size,
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Проверяем, что данные о кофе загрузились
                        coffee?.let { currentCoffee ->
                            CartManager.addCoffee(currentCoffee, selectedSize)
                            // Можно добавить Toast-уведомление, чтобы юзер понял, что всё сработало:
                            // Toast.makeText(context, "Добавлено в корзину!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Добавить в корзину", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Не удалось загрузить данные о кофе")
            }
        }
    }
}