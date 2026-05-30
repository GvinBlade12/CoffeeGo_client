package com.example.coffeeapp.managers

enum class PaymentType {
    CASH, CARD, SBP
}

data class PaymentMethod(
    val id: String,
    val title: String,
    val type: PaymentType,
    val subtitle: String? = null, // Для маски карты
    val isSelected: Boolean = false
)