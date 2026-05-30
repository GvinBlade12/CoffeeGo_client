package com.example.coffeeapp.managers

import androidx.compose.runtime.mutableStateListOf
import com.example.coffeeapp.CoffeeModel

// Моделька для элемента в корзине (сам кофе + выбранный размер + количество)
data class CartItem(
    val coffee: CoffeeModel,
    val size: String,
    var quantity: Int = 1,
    val price: Int = 450 // Пока захардкодим цену, потом можно вынести в бэкенд
)

object CartManager {
    // Специальный список Compose, который сам обновляет экраны при изменении элементов
    val cartItems = mutableStateListOf<CartItem>()

    // Функция добавления товара
    fun addCoffee(coffee: CoffeeModel, size: String) {
        // Проверяем, есть ли уже ТОЧНО ТАКОЙ ЖЕ кофе с ТАКИМ ЖЕ размером в корзине
        val existingItem = cartItems.find { it.coffee.id == coffee.id && it.size == size }

        if (existingItem != null) {
            existingItem.quantity++
            // Небольшой хак для Compose, чтобы он увидел изменение внутри объекта
            val index = cartItems.indexOf(existingItem)
            cartItems[index] = existingItem.copy()
        } else {
            cartItems.add(CartItem(coffee = coffee, size = size))
        }
    }

    // Удаление или уменьшение количества
    fun removeCoffee(cartItem: CartItem) {
        val existingItem = cartItems.find { it.coffee.id == cartItem.coffee.id && it.size == cartItem.size }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
                val index = cartItems.indexOf(existingItem)
                cartItems[index] = existingItem.copy()
            } else {
                cartItems.remove(existingItem)
            }
        }
    }

    // Считаем общую стоимость
    fun getTotalPrice(): Int {
        return cartItems.sumOf { it.price * it.quantity }
    }

    // Очистить корзину после заказа
    fun clear() {
        cartItems.clear()
    }
}