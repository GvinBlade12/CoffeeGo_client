package com.example.coffeeapp.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class FaqItem(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(navController: NavController) {
    val context = LocalContext.current

    val faqList = remember {
        listOf(
            FaqItem("Как отменить случайный заказ?", "Если статус заказа еще 'Готовится', вы можете быстро позвонить нашему бариста по кнопке ниже. Если кофе уже сварен, отмена, к сожалению, невозможна."),
            FaqItem("Где посмотреть мои кофейные бонусы?", "Ваши бонусы привязаны к номеру телефона. Мы планируем добавить их отображение на главном экране в следующем обновлении!"),
            FaqItem("Приложение списало деньги, но заказа нет в истории?", "Не переживайте! Иногда банку нужно до 5 минут на подтверждение. Если заказ не появился, напишите нам в Telegram, прикрепив скриншот чека.")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поддержка", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723)) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // --- БЛОК 1: СВЯЗЬ С НАМИ ---
            item {
                Text("Связаться с нами", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Написать в Telegram
            item {
                SupportContactCard(title = "Написать в Telegram", subtitle = "@coffee_app_support_bot", color = Color(0xFF24A1DE)) {
                    // Системный интент: открываем ссылку в браузере или приложении TG
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/telegram")) // Сюда подставишь ссылку на бота
                    context.startActivity(intent)
                }
            }

            // Позвонить по телефону
            item {
                SupportContactCard(title = "Позвонить в кофейню", subtitle = "8 (800) 555-35-35", color = Color(0xFF2E7D32)) {
                    // Системный интент: открываем номеронабиратель
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:88005553535"))
                    context.startActivity(intent)
                }
            }

            // Написать на Почту
            item {
                SupportContactCard(title = "Написать на Email", subtitle = "support@coffeeapp.ru", color = Color(0xFFE65100)) {
                    // Системный интент: открыть почтовый клиент
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@coffeeapp.ru")
                        putExtra(Intent.EXTRA_SUBJECT, "Вопрос по приложению CoffeeApp")
                    }
                    context.startActivity(Intent.createChooser(intent, "Отправить письмо..."))
                }
            }

            // --- БЛОК 2: FAQ ---
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Часто задаваемые вопросы", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(faqList) { item ->
                FaqRow(faqItem = item)
            }
        }
    }
}

@Composable
fun SupportContactCard(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Text(text = subtitle, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun FaqRow(faqItem: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faqItem.question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF3E2723),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Развернуть",
                    tint = Color.Gray
                )
            }

            // красивая плавная анимация появления текста
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = faqItem.answer, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }
    }
}