package com.example.coffeeapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.compose.rememberNavController
import com.example.coffeeapp.ui.theme.CoffeeAppTheme
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.* // 📥 Импортируем HttpResponse
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String
)

@Composable
fun RegListScreen(navController: NavController) {
    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        containerColor = Color(0xFFF3E5AB),
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
                text = "Регистрация",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF184E50),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Поле ввода ИМЕНИ
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text("Имя") },
                placeholder = { Text("Как вас зовут?") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Иконка пользователя",
                        tint = Color(0xFF184E50)
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEFE6C7),
                    unfocusedContainerColor = Color(0xFFE7CC89),
                    focusedBorderColor = Color(0xFF184E50),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Поле ввода EMAIL
            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text("Email") },
                placeholder = { Text("example@mail.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Иконка почты",
                        tint = Color(0xFF184E50)
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEFE6C7),
                    unfocusedContainerColor = Color(0xFFE7CC89),
                    focusedBorderColor = Color(0xFF184E50),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Поле ввода ПАРОЛЯ
            OutlinedTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                label = { Text("Password") },
                placeholder = { Text("Минимум 6 символов") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Иконка замка",
                        tint = Color(0xFF184E50)
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEFE6C7),
                    unfocusedContainerColor = Color(0xFFE7CC89),
                    focusedBorderColor = Color(0xFF184E50),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                )
            }

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF184E50))
            } else {
                Button(
                    onClick = {
                        if (nameText.isBlank() || emailText.isBlank() || passwordText.isBlank()) {
                            errorMessage = "Пожалуйста, заполните все поля!"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        coroutineScope.launch {
                            try {
                                // 1. Получаем HttpResponse вместо String, чтобы прочитать статус ответа
                                val response: HttpResponse = client.post("http://192.168.0.101:8080/users") {
                                    contentType(ContentType.Application.Json)
                                    setBody(CreateUserRequest(
                                        name = nameText,
                                        email = emailText,
                                        password = passwordText
                                    ))
                                }

                                // 2. Проверяем статус ответа от Ktor сервера
                                if (response.status == HttpStatusCode.Created) {
                                    val responseText = response.body<String>() // "Пользователь создан с ID: X"

                                    // Вытаскиваем цифры (ID) из ответа сервера
                                    val userId = responseText.filter { it.isDigit() }.toIntOrNull() ?: -1

                                    // 3. Сохраняем все данные в сессию на телефоне
                                    SessionManager.saveLoginSession(context, emailText)
                                    SessionManager.saveNickname(context, nameText)
                                    SessionManager.saveUserId(context, userId) // Наш новый метод!

                                    isLoading = false

                                    // Улетаем в каталог
                                    navController.navigate("catalog") {
                                        popUpTo("registration") { inclusive = true }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "Ошибка сервера: ${response.status}"
                                }

                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = e.localizedMessage ?: "Ошибка подключения к серверу"
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF184E50)),
                    elevation = ButtonDefaults.buttonElevation(12.dp),
                    border = BorderStroke(1.dp, Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(48.dp)
                ) {
                    Text("Зарегистрироваться")
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("registration") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = "Уже есть аккаунт? Войти",
                        color = Color(0xFF184E50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun RegPreview(){
    CoffeeAppTheme {
        val fakeNavController = rememberNavController()
        RegListScreen(navController = fakeNavController)
    }
}