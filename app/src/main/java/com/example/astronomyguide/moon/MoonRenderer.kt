package com.example.astronomyguide.opengl.moon

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonRenderer : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var sphereBuffers: MoonSphere.SphereBuffers
    private val moonSphere = MoonSphere()

    private var rotationAngle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // Инициализируем шейдер
        MoonShader.initProgram()

        // Создаем сферу
        sphereBuffers = moonSphere.createSphere(1.0f)

        Log.i("MoonRenderer", "onSurfaceCreated completed")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 0f, 5f,    // Камера
            0f, 0f, 0f,    // Центр
            0f, 1f, 0f     // Вверх
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Анимация вращения
        rotationAngle += 1f
        if (rotationAngle > 360) rotationAngle = 0f

        // Используем шейдер
        MoonShader.useProgram()

        // Матрица модели с вращением
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, 30f, 1f, 0f, 0f)

        // Устанавливаем матрицы
        val vpMatrix = FloatArray(16)
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        MoonShader.setMatrices(vpMatrix, modelMatrix)

        // Устанавливаем источник света (движется вокруг)
        val lightPos = floatArrayOf(
            3f * Math.sin(Math.toRadians(rotationAngle.toDouble())).toFloat(),
            2f,
            3f * Math.cos(Math.toRadians(rotationAngle.toDouble())).toFloat()
        )
        MoonShader.setLightPosition(lightPos)

        // Цвет Луны (серый)
        MoonShader.setColor(floatArrayOf(0.8f, 0.8f, 0.8f))

        // Привязываем буферы
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sphereBuffers.vbo)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sphereBuffers.ibo)

        // Позиция (3 float)
        val posLoc = MoonShader.getPositionLoc()
        if (posLoc >= 0) {
            GLES20.glEnableVertexAttribArray(posLoc)
            GLES20.glVertexAttribPointer(posLoc, 3, GLES20.GL_FLOAT, false, 6 * 4, 0)
        }

        // Нормаль (3 float) - идет после позиции
        val normalLoc = MoonShader.getNormalLoc()
        if (normalLoc >= 0) {
            GLES20.glEnableVertexAttribArray(normalLoc)
            GLES20.glVertexAttribPointer(normalLoc, 3, GLES20.GL_FLOAT, false, 6 * 4, 3 * 4)
        }

        // Рисуем
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, sphereBuffers.indexCount, GLES20.GL_UNSIGNED_SHORT, 0)

        // Отключаем
        if (posLoc >= 0) GLES20.glDisableVertexAttribArray(posLoc)
        if (normalLoc >= 0) GLES20.glDisableVertexAttribArray(normalLoc)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

        // Проверяем ошибки
        var error: Int
        while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
            Log.e("MoonRenderer", "glError: $error")
        }
    }
}