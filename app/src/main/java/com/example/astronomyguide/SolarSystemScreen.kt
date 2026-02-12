package com.example.astronomyguide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.astronomyguide.opengl.OpenGLView

@Composable
fun SolarSystemScreen() {
    val context = LocalContext.current
    var openGLRenderer by remember { mutableStateOf<com.example.astronomyguide.opengl.OpenGLRenderer?>(null) }
    var planetInfo by remember { mutableStateOf("Солнце\nЗвезда\nДиаметр: 1,391,000 км") }
    var showInfoDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Солнечная Система",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF9D71D3),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        // OpenGL View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(horizontal = 8.dp)
        ) {
            AndroidView(
                factory = { ctx ->
                    OpenGLView(ctx).apply {
                        // Сохраняем renderer при создании
                        openGLRenderer = this.renderer
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        // Кнопки управления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Влево
            Button(
                onClick = {
                    openGLRenderer?.selectPrevious()
                    planetInfo = openGLRenderer?.getSelectedPlanetInfo() ?: ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B3FA0)
                ),
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Предыдущая",
                    tint = Color.White
                )
            }

            // Информация
            Button(
                onClick = { showInfoDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9D71D3)
                ),
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Информация",
                    tint = Color.White
                )
            }

            // Вправо
            Button(
                onClick = {
                    openGLRenderer?.selectNext()
                    planetInfo = openGLRenderer?.getSelectedPlanetInfo() ?: ""
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B3FA0)
                ),
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Следующая",
                    tint = Color.White
                )
            }
        }

        // Информационная карточка
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4A1E6D).copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = planetInfo,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    // Диалог с информацией
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    "Солнечная система",
                    color = Color(0xFF4A1E6D),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Выбранный объект:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        planetInfo,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Используйте кнопки ← → для переключения",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF9D71D3)
                    )
                ) {
                    Text("Закрыть")
                }
            },
            containerColor = Color.White
        )
    }
}