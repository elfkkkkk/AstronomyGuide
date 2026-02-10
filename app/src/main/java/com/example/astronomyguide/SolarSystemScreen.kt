package com.example.astronomyguide

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.astronomyguide.opengl.OpenGLView

@Composable
fun SolarSystemScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Солнечная Система",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFF9D71D3),  // LightPurple
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        // OpenGL View занимает остальное пространство
        OpenGLViewComposable(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun OpenGLViewComposable(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        factory = { context ->
            OpenGLView(context)
        },
        modifier = modifier
    )
}