package com.example.coffeeapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.coffeeapp.managers.CartItem
import com.example.coffeeapp.managers.CartManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val cartItems = CartManager.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Корзина", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3E5AB))
            )
        },
        containerColor = Color(0xFFF3E5AB)
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            // Если в корзине пусто
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "В корзине пока пусто ☕", fontSize = 18.sp, color = Color.Gray)
            }
        } else {
            // Если товары есть
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Список товаров
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                // Итог
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Итого:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(text = "${CartManager.getTotalPrice()} ₽", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка заказа
                Button(
                    onClick = {
                        // Тут будет отправка заказа на Ктор-сервер
                        CartManager.clear()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Оформить заказ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF184E50)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.coffee.imageUrl,
                contentDescription = item.coffee.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.coffee.name, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF)
                )
                Text(text = "Размер: ${item.size}", fontSize = 13.sp, color = Color.Gray)
                Text(text = "${item.price * item.quantity} ₽", fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF4DD252))
            }

            // Управление количеством
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { CartManager.removeCoffee(item) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Меньше",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(text = item.quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold)

                Button(
                    onClick = { CartManager.addCoffee(item.coffee, item.size) },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723))
                ) {
                    Text("+", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}