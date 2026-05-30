package com.example.coffeeapp.data

// Простая моделька заказа
data class OrderItem(
    val id: String,          // Уникальный номер заказа
    val date: String,        // Дата заказа
    val items: String,       // Что купили
    val totalPrice: Int,     // Итоговая стоимость
    val status: String       // Статус: "Готов", "Готовится", "Отменён"
)