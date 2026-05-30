package com.example.coffeeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffeeapp.data.OrderItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    // ☕ НАША ЗАТЫЧКА (MOCK-ДАННЫЕ)
    // Когда перепишем сервер, просто заменим этот список на запрос в бд!
    val fakeOrders = listOf(
        OrderItem(
            "№2048",
            "Сегодня, 14:20",
            "Латте Макиато (Бон) x1, Синнабон x1",
            420,
            "Готовится"
        ),
        OrderItem("№1984", "Вчера, 18:05", "Капучино (Стандарт) x2", 360, "Завершен"),
        OrderItem(
            "№1512",
            "24 мая, 11:15",
            "Эспрессо x1, Круассан с шоколадом x1",
            290,
            "Завершен"
        ),
        OrderItem("№1204", "18 мая, 09:30", "Американо x1", 150, "Отменён")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История заказов", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF3E2723))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3E5AB))
            )
        },
        containerColor = Color(0xFFF3E5AB) // Твой фирменный ванильный цвет
    ) { innerPadding ->

        if (fakeOrders.isEmpty()) {
            // Если заказов нет (на случай проверки)
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Вы еще не делали заказов", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            // Сама лента заказов
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp), // Отступы между карточками
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(fakeOrders) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderItem) {
    // Определяем цвет статуса
    val statusColor = when (order.status) {
        "Готовится" -> Color(0xFFE65100) // Оранжевый
        "Завершен" -> Color(0xFF2E7D32)  // Зеленый
        else -> Color(0xFFD32F2F)        // Красный для отмены
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Белая карточка на ванильном фоне выглядит круто
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = order.id, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF3E2723))
                Text(text = order.date, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Что купили
            Text(text = order.items, fontSize = 15.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Статус
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = order.status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Цена
                Text(
                    text = "${order.totalPrice} ₽",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color(0xFF3E2723)
                )
            }
        }
    }
}