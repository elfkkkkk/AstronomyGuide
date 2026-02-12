package com.example.astronomyguide.opengl

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Sphere {

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        uniform mat4 uMVPMatrix;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            gl_PointSize = 5.0;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var mvpMatrixHandle: Int = 0

    private var vertexBuffer: FloatBuffer
    private val vertices: FloatArray

    init {
        // Генерируем вершины сферы
        vertices = generateSphereVertices(1.0f, 20, 20)

        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    private fun generateSphereVertices(radius: Float, stacks: Int, slices: Int): FloatArray {
        val vertices = mutableListOf<Float>()

        for (i in 0..stacks) {
            val phi = Math.PI * i.toDouble() / stacks
            for (j in 0..slices) {
                val theta = 2.0 * Math.PI * j.toDouble() / slices

                val x = (radius * Math.sin(phi) * Math.cos(theta)).toFloat()
                val y = (radius * Math.cos(phi)).toFloat()
                val z = (radius * Math.sin(phi) * Math.sin(theta)).toFloat()

                vertices.add(x)
                vertices.add(y)
                vertices.add(z)
            }
        }

        return vertices.toFloatArray()
    }

    fun draw(
        projectionMatrix: FloatArray,
        viewMatrix: FloatArray,
        modelMatrix: FloatArray,
        color: FloatArray
    ) {
        GLES20.glUseProgram(program)

        val mvpMatrix = FloatArray(16)
        val viewProjectionMatrix = FloatArray(16)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 3,
            GLES20.GL_FLOAT, false,
            3 * 4, vertexBuffer
        )

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertices.size / 3)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}