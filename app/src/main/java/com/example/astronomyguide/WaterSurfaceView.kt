package com.example.astronomyguide.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class WaterSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    private val waterRenderer: WaterRenderer

    init {
        setEGLContextClientVersion(2)
        waterRenderer = WaterRenderer()
        setRenderer(waterRenderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}