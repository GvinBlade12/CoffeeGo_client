package com.example.coffeeapp.data

import kotlinx.serialization.Serializable

@Serializable
data class CoffeeModel(
    val id: Int,
    val name: String,
    val type: String,
    val rating: Double,
    val imageUrl: String
)