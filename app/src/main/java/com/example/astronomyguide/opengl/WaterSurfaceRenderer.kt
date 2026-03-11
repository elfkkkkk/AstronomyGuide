package com.example.astronomyguide.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.*

class WaterSurfaceRenderer : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var program = 0
    private var positionHandle = 0
    private var texCoordHandle = 0
    private var mvpMatrixHandle = 0
    private var timeHandle = 0
    private var textureUniformHandle = 0

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer
    private var waterTextureId = 0
    private var startTime = System.currentTimeMillis()

    init {
        val vertices = floatArrayOf(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            -1f,  1f, 0f,
            1f,  1f, 0f
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

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST) // 2D - не нужен

        // шейдеры
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader())
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader())

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        timeHandle = GLES20.glGetUniformLocation(program, "uTime")
        textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture")

        waterTextureId = createDetailedWaterTexture()
    }

    // функция создания текстуры
    private fun createDetailedWaterTexture(): Int {
        val size = 1024
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                val u = x.toFloat() / size
                val v = y.toFloat() / size

                val baseWave1 = sin(u * 10f * PI.toFloat()) * cos(v * 8f * PI.toFloat())
                val baseWave2 = sin(u * 15f * PI.toFloat() + 2f) * cos(v * 12f * PI.toFloat() + 1f)
                val ripple1 = sin(u * 40f * PI.toFloat()) * sin(v * 35f * PI.toFloat())
                val ripple2 = cos(u * 50f * PI.toFloat() + 3f) * cos(v * 45f * PI.toFloat() + 2f)
                val microRipple1 = sin(u * 120f * PI.toFloat() + v * 80f * PI.toFloat())
                val microRipple2 = cos(u * 150f * PI.toFloat() - v * 130f * PI.toFloat())

                val centerX1 = 0.3f
                val centerY1 = 0.7f
                val dist1 = sqrt((u - centerX1).pow(2) + (v - centerY1).pow(2))
                val circular1 = sin(dist1 * 30f * PI.toFloat()) * exp(-dist1 * 5f)

                val centerX2 = 0.7f
                val centerY2 = 0.3f
                val dist2 = sqrt((u - centerX2).pow(2) + (v - centerY2).pow(2))
                val circular2 = cos(dist2 * 25f * PI.toFloat()) * exp(-dist2 * 4f)

                val turbulence = sin(u * 200f * PI.toFloat() + v * 150f * PI.toFloat()) *
                        cos(v * 180f * PI.toFloat() - u * 160f * PI.toFloat())

                val combinedWave = (
                        baseWave1 * 0.4f +
                                baseWave2 * 0.3f +
                                ripple1 * 0.2f +
                                ripple2 * 0.15f +
                                microRipple1 * 0.1f +
                                microRipple2 * 0.1f +
                                circular1 * 0.25f +
                                circular2 * 0.2f +
                                turbulence * 0.08f
                        ) * 0.5f + 0.5f

                val depthFactor = 1f - sqrt((u - 0.5f).pow(2) + (v - 0.5f).pow(2)) * 1.2f
                val finalHeight = (combinedWave * 0.7f + depthFactor * 0.3f).coerceIn(0f, 1f)

                val red = (0.05f + finalHeight * 0.15f + sin(finalHeight * 10f) * 0.03f).coerceIn(0f, 1f)
                val green = (0.2f + finalHeight * 0.4f + cos(finalHeight * 8f) * 0.05f).coerceIn(0f, 1f)
                val blue = (0.6f + finalHeight * 0.4f + sin(finalHeight * 12f) * 0.07f).coerceIn(0f, 1f)

                var finalRed = red
                var finalGreen = green
                var finalBlue = blue

                if (finalHeight > 0.8f) {
                    val foam = (finalHeight - 0.8f) * 5f
                    finalRed = (red + foam * 0.5f).coerceIn(0f, 1f)
                    finalGreen = (green + foam * 0.5f).coerceIn(0f, 1f)
                    finalBlue = (blue + foam).coerceIn(0f, 1f)
                }

                val color = Color.rgb(
                    (finalRed * 255).toInt(),
                    (finalGreen * 255).toInt(),
                    (finalBlue * 255).toInt()
                )
                bitmap.setPixel(x, y, color)
            }
        }

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
        bitmap.recycle()

        return textures[0]
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        // проекция для 2D
        Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f)
        Matrix.setIdentityM(viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)

        val time = (System.currentTimeMillis() - startTime).toFloat() / 1000f

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform1f(timeHandle, time)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterTextureId)
        GLES20.glUniform1i(textureUniformHandle, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    //  шейдеры
    private fun getVertexShader(): String {
        return """
            attribute vec3 aPosition;
            attribute vec2 aTexCoord;
            uniform mat4 uMVPMatrix;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
                vTexCoord = aTexCoord;
            }
        """.trimIndent()
    }

    private fun getFragmentShader(): String {
        return """
            precision highp float;
            varying vec2 vTexCoord;
            uniform sampler2D uTexture;
            uniform float uTime;
            
            void main() {
                vec2 coord = vTexCoord;
                
                float distortion1 = sin(coord.y * 25.0 + uTime * 3.0) * 0.03;
                float distortion2 = cos(coord.x * 30.0 - uTime * 2.5) * 0.02;
                float distortion3 = sin(coord.x * 60.0 + coord.y * 40.0 + uTime * 5.0) * 0.015;
                float distortion4 = cos(coord.x * 100.0 - coord.y * 80.0 + uTime * 4.0) * 0.01;
                
                coord.x += distortion1 + distortion2 + distortion3;
                coord.y += distortion2 + distortion3 + distortion4;
                
                vec4 color = texture2D(uTexture, coord);
                gl_FragColor = color;
            }
        """.trimIndent()
    }
}