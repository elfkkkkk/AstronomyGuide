package com.example.astronomyguide.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NeptuneRenderer : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var sphere: TexturedSphere
    private var waterTextureId = 0
    private var startTime = System.currentTimeMillis()

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        sphere = TexturedSphere(1.0f, 64, 64)

        waterTextureId = TexturedSphere.createWaterTexture()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspectRatio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 0.1f, 100f)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val time = (System.currentTimeMillis() - startTime).toFloat() / 1000f

        // Позиция камеры
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 0f, 3f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        //  планета вращается
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, time * 20f, 0f, 1f, 0f)  // вращение вокруг Y
        Matrix.rotateM(modelMatrix, 0, 30f, 1f, 0f, 0f)        // наклон

        // для эффекта волн
        sphere.draw(
            projectionMatrix,
            viewMatrix,
            modelMatrix,
            waterTextureId,
            time,
            true
        )
    }
}