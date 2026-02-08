package com.example.astronomyguide.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.example.astronomyguide.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Square(private val context: Context) {  // ← ДОБАВИЛИ КОНТЕКСТ В КОНСТРУКТОР

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec2 vTexCoord;
        varying vec2 texCoord;
        uniform mat4 uMVPMatrix;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            texCoord = vTexCoord;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 texCoord;
        uniform sampler2D uTexture;
        void main() {
            gl_FragColor = texture2D(uTexture, texCoord);
        }
    """.trimIndent()

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var textureHandle: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var textureUniformHandle: Int = 0

    private var textureId: Int = 0  // ID текстуры

    // Координаты вершин квадрата
    private val vertices = floatArrayOf(
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        -0.5f,  0.5f, 0.0f,
        0.5f,  0.5f, 0.0f
    )

    // Координаты текстуры
    private val texCoords = floatArrayOf(
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
    )

    private val vertexBuffer: FloatBuffer
    private val texCoordBuffer: FloatBuffer

    init {
        // Инициализация буферов
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

        // Компиляция шейдеров
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // Создание программы
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        // Загружаем текстуру
        loadTexture()
    }

    private fun loadTexture() {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            // Биндим текстуру
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            // Настраиваем фильтрацию
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )

            // Настраиваем обертывание текстуры
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT
            )

            try {
                // Загружаем bitmap из ресурсов
                // ИСПРАВЬТЕ R.drawable.galaxy_texture на ваше имя файла!
                val bitmap = BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.galaxy_texture  // ← ВАШ ФАЙЛ
                )

                if (bitmap != null) {
                    // Загружаем bitmap в OpenGL
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

                    // Освобождаем bitmap
                    bitmap.recycle()

                    textureId = textureHandle[0]
                } else {
                    // Если текстура не загрузилась, создаем простую текстуру
                    createDefaultTexture(textureHandle[0])
                }
            } catch (e: Exception) {
                // Если ошибка при загрузке текстуры
                createDefaultTexture(textureHandle[0])
            }
        }
    }

    private fun createDefaultTexture(textureHandle: Int) {
        // Создаем простую текстуру (черный фон с белыми точками)
        val pixels = IntArray(64 * 64)
        for (i in pixels.indices) {
            if (i % 8 == 0) {
                pixels[i] = 0xFFFFFFFF.toInt()  // Белые точки
            } else {
                pixels[i] = 0x00000000.toInt()  // Черный фон
            }
        }

        val bitmap = Bitmap.createBitmap(pixels, 64, 64, Bitmap.Config.ARGB_8888)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        textureId = textureHandle
    }

    fun draw(projectionMatrix: FloatArray, viewMatrix: FloatArray, modelMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        // Получаем handle'ы
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        textureHandle = GLES20.glGetAttribLocation(program, "vTexCoord")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture")

        // Вычисляем MVP матрицу
        val mvpMatrix = FloatArray(16)
        val viewProjectionMatrix = FloatArray(16)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)

        // Передаем матрицу в шейдер
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Устанавливаем текстуру
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureUniformHandle, 0)

        // Включаем атрибуты
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 3,
            GLES20.GL_FLOAT, false,
            3 * 4, vertexBuffer
        )

        GLES20.glEnableVertexAttribArray(textureHandle)
        GLES20.glVertexAttribPointer(
            textureHandle, 2,
            GLES20.GL_FLOAT, false,
            2 * 4, texCoordBuffer
        )

        // Рисуем
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // Отключаем атрибуты
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}