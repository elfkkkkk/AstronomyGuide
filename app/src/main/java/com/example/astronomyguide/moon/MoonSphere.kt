package com.example.astronomyguide.opengl.moon

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MoonSphere(radius: Float = 1.0f) {

    data class SphereBuffers(
        val vbo: Int,
        val ibo: Int,
        val indexCount: Int
    )

    fun createSphere(radius: Float, stacks: Int = 32, slices: Int = 32): SphereBuffers {
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        for (i in 0..stacks) {
            val lat = Math.PI / 2 - i * Math.PI / stacks
            val sinLat = Math.sin(lat)
            val cosLat = Math.cos(lat)

            for (j in 0..slices) {
                val lon = 2 * Math.PI * j / slices
                val sinLon = Math.sin(lon)
                val cosLon = Math.cos(lon)

                // Позиция (x, y, z)
                val x = (cosLon * cosLat).toFloat() * radius
                val y = sinLat.toFloat() * radius
                val z = (sinLon * cosLat).toFloat() * radius

                vertices.add(x)
                vertices.add(y)
                vertices.add(z)

                // Нормаль (нормализованная позиция)
                val length = Math.sqrt((x*x + y*y + z*z).toDouble()).toFloat()
                vertices.add(x / length)
                vertices.add(y / length)
                vertices.add(z / length)
            }
        }

        // Индексы для треугольников
        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * (slices + 1) + j).toShort()
                val second = ((i + 1) * (slices + 1) + j).toShort()

                indices.add(first)
                indices.add(second)
                indices.add((first + 1).toShort())

                indices.add(second)
                indices.add((second + 1).toShort())
                indices.add((first + 1).toShort())
            }
        }

        // Создаем VBO
        val vbo = IntArray(1)
        GLES20.glGenBuffers(1, vbo, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])

        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(vertices.toFloatArray()); position(0) }

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.size * 4, vertexBuffer, GLES20.GL_STATIC_DRAW)

        // Создаем IBO
        val ibo = IntArray(1)
        GLES20.glGenBuffers(1, ibo, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0])

        val indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply { put(indices.toShortArray()); position(0) }

        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.size * 2, indexBuffer, GLES20.GL_STATIC_DRAW)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

        return SphereBuffers(vbo[0], ibo[0], indices.size)
    }
}