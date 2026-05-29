package com.example.coffeeapp

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.coffeeapp.ui.theme.currentAppTheme
import java.text.SimpleDateFormat
import java.util.*

fun calculateAge(birthDateMillis: Long): Int {
    val birthCalendar = Calendar.getInstance().apply { timeInMillis = birthDateMillis }
    val todayCalendar = Calendar.getInstance()

    var age = todayCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

    if (todayCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val context = androidx.compose.ui.platform.LocalContext.current

    // 📥 ЗАГРУЖАЕМ ДАННЫЕ ИЗ ПАМЯТИ ПРИ СТАРТЕ ЭКРАНА
    // Никнейм теперь будет подтягиваться автоматически (тот, что ввели при регистрации)
    var nickname by remember { mutableStateOf(SessionManager.getNickname(context)) }

    // Загружаем сохраненные миллисекунды даты рождения
    val savedMillis = remember { SessionManager.getBirthdayMillis(context) }

    // Инициализируем переменные в зависимости от того, были ли данные сохранены ранее
    var birthday by remember {
        mutableStateOf(
            if (savedMillis != 0L) {
                val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                "ДР: ${formatter.format(Date(savedMillis))}"
            } else {
                "Указать дату рождения"
            }
        )
    }

    var userAgeText by remember {
        mutableStateOf(
            if (savedMillis != 0L) {
                "${calculateAge(savedMillis)} лет"
            } else {
                "Возраст не указан"
            }
        )
    }

    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showEditNicknameDialog by remember { mutableStateOf(false) }
    var newNicknameInput by remember { mutableStateOf(nickname) }

    // 🌐 ФУНКЦИЯ-ЗАГОТОВКА ДЛЯ СЕРВЕРА
    // Когда допишешь сервер Ktor, внутри этой функции будет реальный сетевой запрос
    fun updateNicknameOnServer(newNick: String, onResult: (Boolean) -> Unit) {
        // Имитируем, что сервер ответил "Успешно"
        // (Позже здесь будет: client.post("...") и проверка статуса ответа)
        onResult(true)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                avatarUri = uri
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Личный кабинет", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3E5AB))
            )
        },
        containerColor = Color(0xFFF3E5AB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Блок аватарки
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.BottomEnd
            ) {
                if (avatarUri != null) {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = "Аватарка",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF3E2723), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFE7CC89))
                            .border(2.dp, Color(0xFF3E2723), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Заглушка",
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF3E2723)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3E2723))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Изменить фотку",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Блок отображения Никнейма/Имени
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        newNicknameInput = nickname // Передаем текущее имя в текстовое поле перед открытием диалога
                        showEditNicknameDialog = true
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nickname,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(text = userAgeText, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { showDatePicker = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Календарь",
                        tint = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = birthday,
                        color = Color(0xFF2E7D32),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuItem(icon = Icons.Default.List, title = "История заказов") {
                navController.navigate("order_history")
            }
            ProfileMenuItem(icon = Icons.Default.AccountBox, title = "Способы оплаты") {
                navController.navigate("payment_methods")
            }
            ProfileMenuItem(icon = Icons.Default.Call, title = "Поддержка") {
                navController.navigate("support")
            }
            ProfileMenuItem(icon = Icons.Default.Settings, title = "Настройки") {
                showThemeDialog = true
            }
            ProfileMenuItem(icon = Icons.Default.Info, title = "О программе") {}

            ProfileMenuItem(icon = Icons.Default.Favorite, title = "Избранные товары") {
                navController.navigate("favorites")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    SessionManager.clearSession(context)
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Выход",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Выход из аккаунта",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Календарь
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        birthday = "ДР: ${formatter.format(Date(selectedDateMillis))}"

                        val calculatedAge = calculateAge(selectedDateMillis)
                        userAgeText = "$calculatedAge лет"

                        SessionManager.saveBirthdayMillis(context, selectedDateMillis)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = Color(0xFF3E2723))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- ОБНОВЛЕННЫЙ ДИАЛОГ ИЗМЕНЕНИЯ ИМЕНИ ---
    if (showEditNicknameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNicknameDialog = false },
            title = { Text("Изменить никнейм", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newNicknameInput,
                    onValueChange = { newNicknameInput = it },
                    label = { Text("Новое имя") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3E2723),
                        focusedLabelColor = Color(0xFF3E2723)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newNicknameInput.isNotBlank()) {

                        // 1. Сначала стучимся на сервер
                        updateNicknameOnServer(newNicknameInput) { isSuccess ->
                            if (isSuccess) {
                                // 2. Если сервер ответил успешно — сохраняем локально в телефон
                                SessionManager.saveNickname(context, newNicknameInput)

                                // 3. Обновляем переменную состояния, чтобы экран тут же перерисовал новое имя
                                nickname = newNicknameInput

                                showEditNicknameDialog = false
                            }
                        }
                    } else {
                        showEditNicknameDialog = false
                    }
                }) {
                    Text("Сохранить", color = Color(0xFF2E7D32))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNicknameDialog = false }) {
                    Text("Отмена", color = Color.Gray)
                }
            }
        )
    }

    // Выбор темы
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    "Выбор темы приложения",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            SessionManager.saveTheme(context, "light")
                            currentAppTheme.value = "light"
                            showThemeDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5F5F5),
                            contentColor = Color.Black
                        )
                    ) { Text("Светлая тема") }

                    Button(
                        onClick = {
                            SessionManager.saveTheme(context, "dark")
                            currentAppTheme.value = "dark"
                            showThemeDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF121212),
                            contentColor = Color.White
                        )
                    ) { Text("Тёмная тема") }

                    Button(
                        onClick = {
                            SessionManager.saveTheme(context, "coffee")
                            currentAppTheme.value = "coffee"
                            showThemeDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE7CC89),
                            contentColor = Color(0xFF3E2723)
                        )
                    ) { Text("Фирменная кофейная") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Закрыть", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF184E50)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = title, tint = Color.White)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Открыть", tint = Color.LightGray)
        }
    }
}