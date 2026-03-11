package com.example.astronomyguide.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class WaterSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    private val renderer: WaterSurfaceRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = WaterSurfaceRenderer()
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}