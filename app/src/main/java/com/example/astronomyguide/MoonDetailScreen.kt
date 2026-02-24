package com.example.astronomyguide

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavController
import com.example.astronomyguide.opengl.moon.MoonView

@Composable
fun MoonDetailScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Верхняя панель
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFF9D71D3)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад"
                )
            }

            Text(
                text = "Луна",
                color = Color(0xFF9D71D3),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 3D View (занимает 70% экрана)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AndroidView(
                factory = { context ->
                    MoonView(context)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Информация о Луне
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4A1E6D).copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "🌕 О Луне",
                    color = Color(0xFF9D71D3),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Диаметр: 3 474 км\n" +
                            "• Расстояние от Земли: 384 400 км\n" +
                            "• Температура: от -173°C до +127°C\n" +
                            "• Первое посещение: 1969 год (Аполлон-11)",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}