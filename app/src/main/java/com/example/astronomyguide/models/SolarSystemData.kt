package com.example.astronomyguide.data.models

object SolarSystemData {

    val bodies = listOf(
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
            description = "Звезда, центр Солнечной системы\nДиаметр: 1,391,000 км"
        ),
        Planet(
            id = 1,
            name = "Меркурий",
            radius = 0.1f,
            distanceFromSun = 1.5f,
            orbitSpeed = 4.0f,
            rotationSpeed = 3f,
            colorRed = 0.7f,
            colorGreen = 0.7f,
            colorBlue = 0.7f,
            description = "Меркурий\nДиаметр: 4,879 км"
        ),
        Planet(
            id = 2,
            name = "Венера",
            radius = 0.15f,
            distanceFromSun = 2.0f,
            orbitSpeed = 2.5f,
            rotationSpeed = 2f,
            colorRed = 1.0f,
            colorGreen = 0.6f,
            colorBlue = 0.2f,
            description = "Венера\nДиаметр: 12,104 км"
        ),
        Planet(
            id = 3,
            name = "Земля",
            radius = 0.16f,
            distanceFromSun = 2.5f,
            orbitSpeed = 2.0f,
            rotationSpeed = 1f,
            colorRed = 0.0f,
            colorGreen = 0.5f,
            colorBlue = 1.0f,
            description = "Земля\nДиаметр: 12,742 км"
        ),
        Planet(
            id = 4,
            name = "Луна",
            radius = 0.05f,
            distanceFromSun = 2.5f,
            orbitSpeed = 0f,
            rotationSpeed = 5f,
            colorRed = 0.8f,
            colorGreen = 0.8f,
            colorBlue = 0.8f,
            description = "Луна - Спутник Земли\nДиаметр: 3,474 км"
        ),
        Planet(
            id = 5,
            name = "Марс",
            radius = 0.13f,
            distanceFromSun = 3.0f,
            orbitSpeed = 1.5f,
            rotationSpeed = 1f,
            colorRed = 1.0f,
            colorGreen = 0.2f,
            colorBlue = 0.0f,
            description = "Марс\nДиаметр: 6,779 км"
        ),
        Planet(
            id = 6,
            name = "Юпитер",
            radius = 0.35f,
            distanceFromSun = 4.0f,
            orbitSpeed = 0.8f,
            rotationSpeed = 3f,
            colorRed = 0.9f,
            colorGreen = 0.7f,
            colorBlue = 0.5f,
            description = "Юпитер\nДиаметр: 139,820 км"
        ),
        Planet(
            id = 7,
            name = "Сатурн",
            radius = 0.3f,
            distanceFromSun = 5.0f,
            orbitSpeed = 0.6f,
            rotationSpeed = 2f,
            colorRed = 0.9f,
            colorGreen = 0.8f,
            colorBlue = 0.6f,
            description = "Сатурн\nДиаметр: 116,460 км"
        ),
        Planet(
            id = 8,
            name = "Уран",
            radius = 0.25f,
            distanceFromSun = 6.0f,
            orbitSpeed = 0.4f,
            rotationSpeed = 2f,
            colorRed = 0.6f,
            colorGreen = 0.8f,
            colorBlue = 1.0f,
            description = "Уран\nДиаметр: 50,724 км"
        ),
        Planet(
            id = 9,
            name = "Нептун",
            radius = 0.24f,
            distanceFromSun = 7.0f,
            orbitSpeed = 0.3f,
            rotationSpeed = 2f,
            colorRed = 0.0f,
            colorGreen = 0.3f,
            colorBlue = 0.8f,
            description = "Нептун\nДиаметр: 49,244 км"
        )
    )

    // Получить Землю для расчета позиции Луны
    val earth = bodies[3]

    // Радиус орбиты Луны относительно Земли
    const val MOON_DISTANCE = 0.4f
    const val MOON_ORBIT_SPEED = 10f
}