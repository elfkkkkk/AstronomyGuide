package com.example.astronomyguide.opengl

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class BlackHole {

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
            vec2 pos = vTexCoord * 2.0 - 1.0;
            float r = length(pos);
            
            // если пиксель вне круга - делаем прозрачным
            if (r > 1.0) {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
                return;
            }
            
            float center = 1.0 - smoothstep(0.0, 0.4, r);
            
            // Внешнее свечение (от 0.4 до 1.0)
            float glow = smoothstep(0.4, 1.0, r);
            
            // Пульсация
            float pulse = 0.8 + 0.2 * sin(r * 10.0 - uTime * 3.0);
            
            // Цвета: фиолетовый к центру, розовый к краям
            float red = 0.7 + 0.3 * glow;
            float green = 0.2 + 0.2 * glow;
            float blue = 0.8 + 0.2 * glow;
            
            // Добавляем розовый оттенок по краям
            red += glow * 0.3 * pulse;
            blue += glow * 0.2 * pulse;
            
            // Прозрачность: центр непрозрачный, края прозрачнее
            float alpha = 1.0 - (glow * 0.7);
            
            // Смешиваем черный центр со свечением
            vec3 color = mix(vec3(0.0, 0.0, 0.0), vec3(red, green, blue), glow);
            
            gl_FragColor = vec4(color, alpha);
        }
    """.trimIndent()

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var texCoordHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var timeHandle: Int = 0

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer

    private var blackHoleX = -4.5f
    private val speed = 0.012f
    private val leftBound = -5.0f
    private val rightBound = 5.0f

    init {
        setupBuffers()
        setupShaders()
    }

    private fun setupBuffers() {
        val vertices = floatArrayOf(
            -1.0f, -1.0f, 0f,
            1.0f, -1.0f, 0f,
            -1.0f,  1.0f, 0f,
            1.0f,  1.0f, 0f
        )

        val texCoords = floatArrayOf(
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
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
    }

    fun update() {
        blackHoleX += speed
        if (blackHoleX > rightBound) {
            blackHoleX = leftBound
        }
    }

    fun draw(projectionMatrix: FloatArray, viewMatrix: FloatArray, time: Float) {
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        timeHandle = GLES20.glGetUniformLocation(program, "uTime")

        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)

        // Позиция
        Matrix.translateM(modelMatrix, 0, blackHoleX, 0.0f, -2.8f)

        // Медленное вращение всего объекта
        Matrix.rotateM(modelMatrix, 0, time * 5, 0f, 0f, 1f)

        val mvpMatrix = FloatArray(16)
        val viewProjectionMatrix = FloatArray(16)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform1f(timeHandle, time)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        // Включаем прозрачность
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisable(GLES20.GL_BLEND)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}