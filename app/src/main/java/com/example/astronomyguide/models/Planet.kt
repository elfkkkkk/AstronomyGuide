package com.example.astronomyguide.data.models

data class Planet(
    val id: Int,
    val name: String,
    val radius: Float,
    val distanceFromSun: Float,
    val orbitSpeed: Float,
    val rotationSpeed: Float,
    val colorRed: Float,
    val colorGreen: Float,
    val colorBlue: Float,
    val description: String
)