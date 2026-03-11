package com.example.astronomyguide

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.astronomyguide.data.models.Planet
import com.example.astronomyguide.opengl.NeptuneView
import com.example.astronomyguide.opengl.WaterSurfaceView  // ← ИМПОРТ

@Composable
fun PlanetDetailScreen(
    navController: NavController,
    planet: Planet
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
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
                text = planet.name,
                color = Color(0xFF9D71D3),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(48.dp))
        }

        // ============ ИЗОБРАЖЕНИЕ ============
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4A1E6D).copy(alpha = 0.5f)
            )
        ) {
            if (planet.id == 9) { // Нептун
                AndroidView(
                    factory = { ctx ->
                        NeptuneView(ctx)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                // Обычная картинка для остальных планет
                planet.imageResId?.let { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = planet.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }

        // Информация о планете
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4A1E6D).copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "📋 Характеристики",
                    color = Color(0xFF9D71D3),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                planet.description.split("\n").forEach { line ->
                    Text(
                        text = "• $line",
                        color = Color.White,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Интересный факт: ${getFunFact(planet.name)}",
                    color = Color(0xFFB39DDB),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun getFunFact(planetName: String): String {
    return when(planetName) {
        "Солнце" -> "Солнце составляет 99.86% массы всей Солнечной системы"
        "Меркурий" -> "День на Меркурии длится дольше года (176 земных дней)"
        "Венера" -> "Венера вращается в противоположную сторону относительно других планет"
        "Земля" -> "Единственная известная планета с жизнью"
        "Луна" -> "Луна постепенно удаляется от Земли на 3.8 см каждый год"
        "Марс" -> "На Марсе находится самая высокая гора в Солнечной системе (Олимп)"
        "Юпитер" -> "Юпитер имеет 95 спутников"
        "Сатурн" -> "Кольца Сатурна состоят из 93% воды в виде льда"
        "Уран" -> "Уран вращается на боку, ось наклонена на 98°"
        "Нептун" -> "Ветры на Нептуне достигают скорости 2100 км/ч"
        else -> "Уникальный объект Солнечной системы"
    }
}