package com.example.astronomyguide.data.models

import com.example.astronomyguide.R

object SolarSystemData {

    val bodies = listOf(
        // 0 - Солнце
        Planet(
            id = 0,
            name = "Солнце",
            radius = 0.5f,
            distanceFromSun = 0f,
            orbitSpeed = 0f,
            rotationSpeed = 2f,
            colorRed = 1.0f,
            colorGreen = 0.8f,
            colorBlue = 0.0f,
            description = "Солнце\nЗвезда\nДиаметр: 1,391,000 км",
            imageResId = R.drawable.sun
        ),
        // 1 - Меркурий
        Planet(
            id = 1,
            name = "Меркурий",
            radius = 0.1f,
            distanceFromSun = 2.0f,
            orbitSpeed = 4.0f,
            rotationSpeed = 3f,
            colorRed = 0.7f,
            colorGreen = 0.7f,
            colorBlue = 0.7f,
            description = "Меркурий\nБлижайшая к Солнцу планета\nДиаметр: 4,879 км",
            imageResId = R.drawable.mercury
        ),
        // 2 - Венера
        Planet(
            id = 2,
            name = "Венера",
            radius = 0.15f,
            distanceFromSun = 2.8f,
            orbitSpeed = 2.5f,
            rotationSpeed = 2f,
            colorRed = 1.0f,
            colorGreen = 0.6f,
            colorBlue = 0.2f,
            description = "Венера\nВторая планета от Солнца\nДиаметр: 12,104 км",
            imageResId = R.drawable.venus
        ),
        // 3 - Земля
        Planet(
            id = 3,
            name = "Земля",
            radius = 0.16f,
            distanceFromSun = 3.6f,
            orbitSpeed = 2.0f,
            rotationSpeed = 1f,
            colorRed = 0.0f,
            colorGreen = 0.5f,
            colorBlue = 1.0f,
            description = "Земля\nТретья планета\nДиаметр: 12,742 км",
            imageResId = R.drawable.earth
        ),
        // 4 - Луна
        Planet(
            id = 4,
            name = "Луна",
            radius = 0.05f,
            distanceFromSun = 3.6f,
            orbitSpeed = 0f,
            rotationSpeed = 5f,
            colorRed = 0.8f,
            colorGreen = 0.8f,
            colorBlue = 0.8f,
            description = "Луна\nСпутник Земли\nДиаметр: 3,474 км",
            // imageResId = R.drawable.moon
        ),
        // 5 - Марс
        Planet(
            id = 5,
            name = "Марс",
            radius = 0.13f,
            distanceFromSun = 4.4f,
            orbitSpeed = 1.5f,
            rotationSpeed = 1f,
            colorRed = 1.0f,
            colorGreen = 0.2f,
            colorBlue = 0.0f,
            description = "Марс\nЧетвертая планета\nДиаметр: 6,779 км",
            imageResId = R.drawable.mars
        ),
        // 6 - Юпитер
        Planet(
            id = 6,
            name = "Юпитер",
            radius = 0.35f,
            distanceFromSun = 5.8f,
            orbitSpeed = 0.8f,
            rotationSpeed = 3f,
            colorRed = 0.9f,
            colorGreen = 0.7f,
            colorBlue = 0.5f,
            description = "Юпитер\nПятая планета\nДиаметр: 139,820 км",
            imageResId = R.drawable.jupiter
        ),
        // 7 - Сатурн
        Planet(
            id = 7,
            name = "Сатурн",
            radius = 0.3f,
            distanceFromSun = 7.2f,
            orbitSpeed = 0.6f,
            rotationSpeed = 2f,
            colorRed = 0.9f,
            colorGreen = 0.8f,
            colorBlue = 0.6f,
            description = "Сатурн\nШестая планета\nДиаметр: 116,460 км",
            imageResId = R.drawable.saturn
        ),
        // 8 - Уран
        Planet(
            id = 8,
            name = "Уран",
            radius = 0.25f,
            distanceFromSun = 8.6f,
            orbitSpeed = 0.4f,
            rotationSpeed = 2f,
            colorRed = 0.6f,
            colorGreen = 0.8f,
            colorBlue = 1.0f,
            description = "Уран\nСедьмая планета\nДиаметр: 50,724 км",
            imageResId = R.drawable.uranus
        ),
        // 9 - Нептун
        Planet(
            id = 9,
            name = "Нептун",
            radius = 0.24f,
            distanceFromSun = 10.0f,
            orbitSpeed = 0.3f,
            rotationSpeed = 2f,
            colorRed = 0.0f,
            colorGreen = 0.3f,
            colorBlue = 0.8f,
            description = "Нептун\nВосьмая планета\nДиаметр: 49,244 км",
            imageResId = R.drawable.neptune  // ← Заменим позже на водную гладь
        )
    )

    val earth = bodies[3]
    const val MOON_DISTANCE = 0.6f
    const val MOON_ORBIT_SPEED = 10f
}