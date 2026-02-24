package com.example.astronomyguide.opengl.moon

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class MoonView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    private val moonRenderer: MoonRenderer

    init {
        setEGLContextClientVersion(2)
        moonRenderer = MoonRenderer()
        setRenderer(moonRenderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}