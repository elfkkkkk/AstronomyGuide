package com.example.astronomyguide.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class WaterRenderer : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        uniform mat4 uMVPMatrix;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 vTexCoord;
        uniform float uTime;
        
        void main() {
            // Создаем эффект волн
            float wave1 = sin(vTexCoord.x * 20.0 + uTime * 3.0) * cos(vTexCoord.y * 15.0 + uTime * 2.0);
            float wave2 = sin(vTexCoord.x * 30.0 - uTime * 4.0) * cos(vTexCoord.y * 25.0 + uTime * 3.0);
            float waves = (wave1 + wave2) * 0.3;
            
            // Цвета воды (голубой/синий с бликами)
            float r = 0.0 + waves * 0.2;
            float g = 0.3 + waves * 0.5;
            float b = 0.9 + waves * 0.3;
            
            // Блики
            float specular = pow(max(0.0, sin(vTexCoord.x * 40.0 + uTime) * sin(vTexCoord.y * 40.0 + uTime)), 2.0);
            r += specular * 0.3;
            g += specular * 0.3;
            b += specular * 0.5;
            
            gl_FragColor = vec4(r, g, b, 1.0);
        }
    """.trimIndent()

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var texCoordHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var timeHandle: Int = 0

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer
    private var time = 0f

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    init {
        setupBuffers()
        setupShaders()
    }

    private fun setupBuffers() {
        // Вершины для прямоугольника во весь экран
        val vertices = floatArrayOf(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            -1f,  1f, 0f,
            1f,  1f, 0f
        )

        val texCoords = floatArrayOf(
            0f, 1f,  // В OpenGL текстуры начинаются с нижнего левого угла
            1f, 1f,
            0f, 0f,
            1f, 0f
        )

        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val tb = ByteBuffer.allocateDirect(texCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        texCoordBuffer = tb.asFloatBuffer()
        texCoordBuffer.put(texCoords)
        texCoordBuffer.position(0)
    }

    private fun setupShaders() {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        // Проверка ошибок
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            android.util.Log.e("WaterRenderer", "Program link failed")
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)  // Не нужен для 2D
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // Простая ортографическая проекция для 2D
        Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f)
        Matrix.setIdentityM(viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        time += 0.03f

        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        timeHandle = GLES20.glGetUniformLocation(program, "uTime")

        // Упрощенная матрица
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform1f(timeHandle, time)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)

        // Проверка ошибок
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            android.util.Log.e("WaterRenderer", "OpenGL error: $error")
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        // Проверка компиляции
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            val log = GLES20.glGetShaderInfoLog(shader)
            android.util.Log.e("WaterRenderer", "Shader compile error: $log")
        }

        return shader
    }
}