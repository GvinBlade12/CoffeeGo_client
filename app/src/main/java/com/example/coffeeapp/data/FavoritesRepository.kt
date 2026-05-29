package com.example.coffeeapp.data // подправь пакет под свой проект

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

// Моделька запроса (1-в-1 как на сервере, чтобы Ktor мог сериализовать её в JSON)
@Serializable
data class FavoriteRequest(
    val userId: Int,
    val coffeeId: Int
)

object FavoritesRepository {
    // Твой базовый URL сервера (как в Postman)
    private const val BASE_URL = "http://192.168.0.101:8080"

    // 1. Запрос GET: получить список ID избранного для юзера
    suspend fun getFavorites(client: HttpClient, userId: Int): List<Int> {
        return try {
            client.get("$BASE_URL/favorites/$userId").body()
        } catch (e: Exception) {
            emptyList() // Если нет интернета или сервер упал — возвращаем пустой список
        }
    }

    // 2. Запрос POST: добавить в избранное
    suspend fun addFavorite(client: HttpClient, userId: Int, coffeeId: Int): Boolean {
        return try {
            val response = client.post("$BASE_URL/favorites") {
                contentType(ContentType.Application.Json)
                setBody(FavoriteRequest(userId, coffeeId))
            }
            response.status == HttpStatusCode.Created
        } catch (e: Exception) {
            false
        }
    }

    // 3. Запрос DELETE: удалить из избранного
    suspend fun removeFavorite(client: HttpClient, userId: Int, coffeeId: Int): Boolean {
        return try {
            val response = client.delete("$BASE_URL/favorites") {
                contentType(ContentType.Application.Json)
                setBody(FavoriteRequest(userId, coffeeId))
            }
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }
}