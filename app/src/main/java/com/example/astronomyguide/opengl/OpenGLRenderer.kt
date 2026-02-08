package com.example.astronomyguide.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var square: Square
    private lateinit var cube: Cube

    private var angle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        square = Square(context)
        cube = Cube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height

        // Правильная проекционная матрица для 2D фона
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 100f)

        // Видовая матрица - смотрим прямо
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 0f, 3f,      // Камера смотрит с расстояния 3
            0f, 0f, 0f,      // Смотрим в центр
            0f, 1f, 0f       // Вверх - по оси Y
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Анимация вращения куба
        angle += 1f
        if (angle > 360) angle = 0f

        // 1. Рисуем фон ПЕРВЫМ
        drawBackground()

        // 2. Рисуем куб поверх фона
        drawCube()
    }

    private fun drawBackground() {
        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.translateM(modelMatrix, 0, 0f, 0f, 0f)

        // Масштабируем по размерам экрана
        val ratio = 1.0f
        val scaleX = 10f * ratio  // Горизонтальный масштаб
        val scaleY = 10f          // Вертикальный масштаб
        Matrix.scaleM(modelMatrix, 0, scaleX, scaleY, 1f)

        square.draw(projectionMatrix, viewMatrix, modelMatrix)
    }

    private fun drawCube() {
        Matrix.setIdentityM(modelMatrix, 0)

        // Вращаем куб
        Matrix.rotateM(modelMatrix, 0, angle, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, 30f, 1f, 0f, 0f)

        // Куб в центре, ближе к камере чем фон
        Matrix.translateM(modelMatrix, 0, 0f, 0f, 0f)

        // Размер куба
        Matrix.scaleM(modelMatrix, 0, 0.3f, 0.3f, 0.3f)

        cube.draw(projectionMatrix, viewMatrix, modelMatrix)
    }
}