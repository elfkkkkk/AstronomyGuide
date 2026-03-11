package com.example.astronomyguide.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.example.astronomyguide.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.*

class TexturedSphere {

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        varying vec3 vPositionWorld;
        uniform mat4 uMVPMatrix;
        uniform mat4 uModelMatrix;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vTexCoord = aTexCoord;
            vPositionWorld = (uModelMatrix * vPosition).xyz;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision highp float;
        varying vec2 vTexCoord;
        varying vec3 vPositionWorld;
        uniform sampler2D uTexture;
        uniform float uTime;
        uniform bool uIsNeptune;  // Флаг для Нептуна (водная гладь)
        
        void main() {
            vec2 coord = vTexCoord;
            
            if (uIsNeptune) {
                float wave1 = sin(vPositionWorld.x * 15.0 + uTime * 3.0) * 
                    cos(vPositionWorld.z * 12.0 + uTime * 2.0) * 0.02;
                float wave2 = sin(vPositionWorld.y * 20.0 + uTime * 4.0) * 
                    cos(vPositionWorld.x * 18.0 - uTime * 3.0) * 0.01;
                
                coord.x += wave1;
                coord.y += wave2;
            }
            
            vec4 color = texture2D(uTexture, coord);
            gl_FragColor = color;
        }
    """.trimIndent()

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var texCoordHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var modelMatrixHandle: Int = 0
    private var textureUniformHandle: Int = 0
    private var timeHandle: Int = 0
    private var isNeptuneHandle: Int = 0

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texCoordBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private var indexCount = 0

    // Перегрузка конструктора для создания сферы с параметрами
    constructor() {
        setupBuffers(1.0f, 48, 48)
        setupShaders()
    }

    constructor(radius: Float, stacks: Int, slices: Int) {
        setupBuffers(radius, stacks, slices)
        setupShaders()
    }

    private fun setupBuffers(radius: Float, stacks: Int, slices: Int) {
        val (vertices, texCoords, indices) = generateSphereWithIndices(radius, stacks, slices)
        indexCount = indices.size

        // Буфер вершин
        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // Буфер текстурных координат
        val tb = ByteBuffer.allocateDirect(texCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        texCoordBuffer = tb.asFloatBuffer()
        texCoordBuffer.put(texCoords)
        texCoordBuffer.position(0)

        // Буфер индексов
        val ib = ByteBuffer.allocateDirect(indices.size * 2)
        ib.order(ByteOrder.nativeOrder())
        indexBuffer = ib.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    private fun generateSphereWithIndices(radius: Float, stacks: Int, slices: Int): Triple<FloatArray, FloatArray, ShortArray> {
        val vertices = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        // Генерируем вершины
        for (i in 0..stacks) {
            val phi = Math.PI * i.toDouble() / stacks
            val sinPhi = Math.sin(phi)
            val cosPhi = Math.cos(phi)

            for (j in 0..slices) {
                val theta = 2.0 * Math.PI * j.toDouble() / slices
                val sinTheta = Math.sin(theta)
                val cosTheta = Math.cos(theta)

                val x = (radius * sinPhi * cosTheta).toFloat()
                val y = (radius * cosPhi).toFloat()
                val z = (radius * sinPhi * sinTheta).toFloat()

                vertices.add(x)
                vertices.add(y)
                vertices.add(z)

                val u = (j.toFloat() / slices)
                val v = (i.toFloat() / stacks)
                texCoords.add(u)
                texCoords.add(v)
            }
        }

        // Генерируем индексы для треугольников
        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * (slices + 1) + j).toShort()
                val second = ((i + 1) * (slices + 1) + j).toShort()
                val third = (i * (slices + 1) + j + 1).toShort()
                val fourth = ((i + 1) * (slices + 1) + j + 1).toShort()

                // Первый треугольник
                indices.add(first)
                indices.add(second)
                indices.add(third)

                // Второй треугольник
                indices.add(second)
                indices.add(fourth)
                indices.add(third)
            }
        }

        return Triple(vertices.toFloatArray(), texCoords.toFloatArray(), indices.toShortArray())
    }

    private fun setupShaders() {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    fun draw(
        projectionMatrix: FloatArray,
        viewMatrix: FloatArray,
        modelMatrix: FloatArray,
        textureId: Int,
        time: Float = 0f,
        isNeptune: Boolean = false
    ) {
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        modelMatrixHandle = GLES20.glGetUniformLocation(program, "uModelMatrix")
        textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture")
        timeHandle = GLES20.glGetUniformLocation(program, "uTime")
        isNeptuneHandle = GLES20.glGetUniformLocation(program, "uIsNeptune")

        val mvpMatrix = FloatArray(16)
        val viewProjectionMatrix = FloatArray(16)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0)
        GLES20.glUniform1f(timeHandle, time)
        GLES20.glUniform1i(isNeptuneHandle, if (isNeptune) 1 else 0)

        // Текстура
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureUniformHandle, 0)

        // Вершины
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // Текстурные координаты
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        // Рисуем треугольниками по индексам
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    companion object {
        fun loadTexture(context: Context, resId: Int): Int {
            val textureHandle = IntArray(1)
            GLES20.glGenTextures(1, textureHandle, 0)

            if (textureHandle[0] != 0) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

                val bitmap = BitmapFactory.decodeResource(context.resources, resId)
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
                bitmap.recycle()
            }

            return textureHandle[0]
        }

        fun createWaterTexture(): Int {
            val size = 1024
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

            for (x in 0 until size) {
                for (y in 0 until size) {
                    val u = x.toFloat() / size
                    val v = y.toFloat() / size

                    // паттерн волн
                    val wave1 = sin(u * 20f * PI.toFloat()) * cos(v * 15f * PI.toFloat())
                    val wave2 = sin(u * 30f * PI.toFloat() + 2f) * cos(v * 25f * PI.toFloat())
                    val wave3 = sin(u * 50f * PI.toFloat()) * sin(v * 40f * PI.toFloat())

                    val height = (wave1 * 0.5f + wave2 * 0.3f + wave3 * 0.2f) * 0.5f + 0.5f

                    val red = (0.1f + height * 0.2f).coerceIn(0f, 1f)
                    val green = (0.3f + height * 0.4f).coerceIn(0f, 1f)
                    val blue = (0.8f + height * 0.2f).coerceIn(0f, 1f)

                    val color = android.graphics.Color.rgb(
                        (red * 255).toInt(),
                        (green * 255).toInt(),
                        (blue * 255).toInt()
                    )
                    bitmap.setPixel(x, y, color)
                }
            }

            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()

            return textures[0]
        }
    }
}