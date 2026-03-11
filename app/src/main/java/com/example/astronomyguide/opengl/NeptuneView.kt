package com.example.astronomyguide.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class NeptuneView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    private val renderer: NeptuneRenderer

    init {
        setEGLContextClientVersion(2)
        renderer = NeptuneRenderer()
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}