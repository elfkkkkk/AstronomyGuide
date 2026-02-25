package com.example.astronomyguide.opengl

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube {

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        uniform mat4 uMVPMatrix;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
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

    private val vertices = floatArrayOf(
        -0.5f, -0.5f,  0.5f,
        0.5f, -0.5f,  0.5f,
        -0.5f,  0.5f,  0.5f,
        0.5f,  0.5f,  0.5f,
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        -0.5f,  0.5f, -0.5f,
        0.5f,  0.5f, -0.5f
    )

    private val indices = shortArrayOf(
        0, 1, 2, 2, 1, 3,
        4, 6, 5, 5, 6, 7,
        2, 3, 6, 6, 3, 7,
        0, 4, 1, 1, 4, 5,
        0, 2, 4, 4, 2, 6,
        1, 5, 3, 3, 5, 7
    )

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    private val color = floatArrayOf(1.0f, 1.0f, 1.0f, 0.35f)

    init {
        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val ib = ByteBuffer.allocateDirect(indices.size * 2)
        ib.order(ByteOrder.nativeOrder())
        indexBuffer = ib.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    fun draw(projectionMatrix: FloatArray, viewMatrix: FloatArray, modelMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        val mvpMatrix = FloatArray(16)
        val viewProjectionMatrix = FloatArray(16)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 3,
            GLES20.GL_FLOAT, false,
            3 * 4, vertexBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}