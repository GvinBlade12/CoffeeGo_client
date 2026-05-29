package com.example.coffeeapp

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(navController: NavController) {
    // Храним список способов оплаты в состоянии (State)
    val paymentList = remember {
        mutableStateListOf(
            PaymentMethod("1", "Наличными при получении", PaymentType.CASH, isSelected = true),
            PaymentMethod("2", "Система быстрых платежей (СБП)", PaymentType.SBP),
            PaymentMethod("3", "Visa", PaymentType.CARD, subtitle = "•••• 5678")
        )
    }

    var showAddCardDialog by remember { mutableStateOf(false) }
    var cardNumberInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Способы оплаты", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF3E2723))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3E5AB))
            )
        },
        containerColor = Color(0xFFF3E5AB)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Выберите способ оплаты по умолчанию:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF3E2723),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Список способов
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(paymentList) { method ->
                    PaymentMethodCard(
                        method = method,
                        onSelect = { selectedMethod ->
                            // Сбрасываем выбор у всех и ставим текущему
                            val index = paymentList.indexOfFirst { it.id == selectedMethod.id }
                            if (index != -1) {
                                for (i in paymentList.indices) {
                                    paymentList[i] = paymentList[i].copy(isSelected = (i == index))
                                }
                            }
                        }
                    )
                }
            }

            // Кнопка добавления новой карты
            Button(
                onClick = { showAddCardDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF184E50)), // Твой глубокий цвет менюшек
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Привязать новую карту", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Диалог привязки фейковой карты
    if (showAddCardDialog) {
        AlertDialog(
            onDismissRequest = { showAddCardDialog = false },
            title = { Text("Привязка карты", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Введите 16 знаков номера карты (имитация):", fontSize = 14.sp)
                    OutlinedTextField(
                        value = cardNumberInput,
                        onValueChange = { input ->
                            if (input.length <= 16 && input.all { it.isDigit() }) {
                                cardNumberInput = input
                            }
                        },
                        label = { Text("Номер карты") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3E2723),
                            focusedLabelColor = Color(0xFF3E2723)
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (cardNumberInput.length == 16) {
                        val lastFour = cardNumberInput.takeLast(4)
                        // Добавляем новую карту в наш реактивный список
                        paymentList.add(
                            PaymentMethod(
                                id = System.currentTimeMillis().toString(),
                                title = "Mastercard",
                                type = PaymentType.CARD,
                                subtitle = "•••• $lastFour"
                            )
                        )
                        cardNumberInput = ""
                        showAddCardDialog = false
                    }
                }) {
                    Text("Добавить", color = Color(0xFF2E7D32))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCardDialog = false }) {
                    Text("Отмена", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun PaymentMethodCard(method: PaymentMethod, onSelect: (PaymentMethod) -> Unit) {
    val isSelected = method.isSelected
    val borderColor = if (isSelected) Color(0xFF184E50) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onSelect(method) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = method.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF3E2723)
                )
                if (method.subtitle != null) {
                    Text(
                        text = method.subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Выбрано",
                    tint = Color(0xFF184E50),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // Пустой кружочек, когда не выбрано
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(2.dp, Color.LightGray, RoundedCornerShape(12.dp))
                )
            }
        }
    }
}