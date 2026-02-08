package com.example.astronomyguide.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class OpenGLView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    init {
        // Настраиваем контекст OpenGL ES 2.0
        setEGLContextClientVersion(2)

        // Создаем рендерер с контекстом
        setRenderer(OpenGLRenderer(context))

        // Устанавливаем режим отрисовки
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}