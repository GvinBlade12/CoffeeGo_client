package com.example.coffeeapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.launch

// Моделька пользователя, которую нам возвращает бэкенд при GET /users
@kotlinx.serialization.Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val password: String
)

@Composable
fun LoginScreen(navController: NavController) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        containerColor = Color(0xFFF3E5AB), // Твой фирменный кофейный цвет
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 16.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "CoffeeGo",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF184E50)
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Вход в аккаунт",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF184E50),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Поле Email
            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Почта", tint = Color(0xFF184E50))
                },
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEFE6C7),
                    unfocusedContainerColor = Color(0xFFE7CC89),
                    focusedBorderColor = Color(0xFF184E50),
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Поле Пароля
            OutlinedTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Пароль", tint = Color(0xFF184E50))
                },
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEFE6C7),
                    unfocusedContainerColor = Color(0xFFE7CC89),
                    focusedBorderColor = Color(0xFF184E50),
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, modifier = Modifier.padding(horizontal = 32.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF184E50))
            } else {
                // Кнопка Логина
                Button(
                    onClick = {
                        if (emailText.isBlank() || passwordText.isBlank()) {
                            errorMessage = "Заполните все поля!"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        coroutineScope.launch {
                            try {
                                // Запрашиваем всех пользователей с сервера
                                val users: List<UserResponse> = client.get("http://192.168.0.101:8080/users").body()

                                // Ищем юзера с совпадающими email и password
                                val matchedUser = users.find { it.email == emailText && it.password == passwordText }

                                isLoading = false

                                if (matchedUser != null) {
                                    // Сохраняем сессию только если пользователь РЕАЛЬНО найден и пароль совпал!
                                    SessionManager.saveLoginSession(context, emailText)
                                    SessionManager.saveUserId(context, matchedUser.id)

                                    // Летим в каталог
                                    navController.navigate("catalog") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    // Если не нашли — ругаемся
                                    errorMessage = "Неверный Email или Пароль, либо аккаунт не существует!"
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = "Ошибка сети: ${e.localizedMessage}"
                            }

                            // ОТСЮДА СТРОЧКИ УБРАЛИ, чтобы они не срабатывали при ошибках!
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF184E50)),
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 32.dp).height(48.dp)
                ) {
                    Text("Войти")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Кнопка перехода на экран Регистрации
                TextButton(
                    onClick = {
                        navController.navigate("registration")
                    }
                ) {
                    Text("Нет аккаунта? Зарегистрироваться", color = Color(0xFF184E50))
                }
            }
        }
    }
}
