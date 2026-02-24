package com.example.astronomyguide.opengl.moon

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

object MoonShader {

    private var programId = 0
    private var posLoc = -1
    private var normalLoc = -1
    private var mvpMatrixLoc = -1
    private var modelMatrixLoc = -1
    private var lightPosLoc = -1
    private var colorLoc = -1

    fun initProgram() {
        if (programId != 0 && GLES20.glIsProgram(programId)) {
            Log.i("MoonShader", "Program already exists")
            return
        }

        Log.i("MoonShader", "Creating moon shader program...")

        // Вершинный шейдер с нормалями для модели Фонга
        val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            attribute vec4 vPosition;
            attribute vec3 aNormal;
            varying vec3 vNormal;
            varying vec3 vPositionEye;
            void main() {
                vec3 normal = mat3(uModelMatrix) * aNormal;
                vNormal = normalize(normal);
                vec4 posEye = uModelMatrix * vPosition;
                vPositionEye = posEye.xyz / posEye.w;
                gl_Position = uMVPMatrix * vPosition;
            }
        """.trimIndent()

        // Фрагментный шейдер с моделью Фонга
        val fragmentShaderCode = """
            precision mediump float;
            uniform vec3 uLightPos;
            uniform vec3 uColor;
            varying vec3 vNormal;
            varying vec3 vPositionEye;
            void main() {
                vec3 N = normalize(vNormal);
                vec3 L = normalize(uLightPos - vPositionEye);
                vec3 V = normalize(-vPositionEye);
                vec3 R = reflect(-L, N);
                
                // Ambient
                float ambient = 0.2;
                
                // Diffuse (Ламберт)
                float diffuse = max(dot(N, L), 0.0);
                
                // Specular (Фонг)
                float specular = 0.0;
                if (diffuse > 0.0) {
                    specular = pow(max(dot(R, V), 0.0), 32.0);
                }
                
                vec3 color = uColor * (ambient + 0.8 * diffuse) + vec3(1.0) * specular * 0.5;
                gl_FragColor = vec4(color, 1.0);
            }
        """.trimIndent()

        val vertexShader = loadAndCompileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode, "Moon VS")
        val fragmentShader = loadAndCompileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode, "Moon FS")

        programId = GLES20.glCreateProgram()
        if (programId == 0) {
            Log.e("MoonShader", "Failed to create program")
            return
        }

        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)
        GLES20.glLinkProgram(programId)

        checkLinkStatus(programId, "Moon shader program")

        if (GLES20.glIsProgram(programId)) {
            posLoc = GLES20.glGetAttribLocation(programId, "vPosition")
            normalLoc = GLES20.glGetAttribLocation(programId, "aNormal")
            mvpMatrixLoc = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
            modelMatrixLoc = GLES20.glGetUniformLocation(programId, "uModelMatrix")
            lightPosLoc = GLES20.glGetUniformLocation(programId, "uLightPos")
            colorLoc = GLES20.glGetUniformLocation(programId, "uColor")

            Log.i("MoonShader", "Locations - pos:$posLoc, normal:$normalLoc, mvp:$mvpMatrixLoc, model:$modelMatrixLoc, light:$lightPosLoc, color:$colorLoc")
        }

        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
    }

    fun useProgram() {
        if (programId == 0 || !GLES20.glIsProgram(programId)) {
            initProgram()
        }
        if (programId != 0) {
            GLES20.glUseProgram(programId)
        }
    }

    fun setMatrices(vpMatrix: FloatArray, modelMatrix: FloatArray) {
        if (programId == 0) return

        val mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrix, 0)
    }

    fun setLightPosition(lightPos: FloatArray) {
        if (programId == 0) return
        GLES20.glUniform3f(lightPosLoc, lightPos[0], lightPos[1], lightPos[2])
    }

    fun setColor(color: FloatArray) {
        if (programId == 0) return
        GLES20.glUniform3f(colorLoc, color[0], color[1], color[2])
    }

    fun getPositionLoc() = posLoc
    fun getNormalLoc() = normalLoc

    private fun loadAndCompileShader(type: Int, code: String, name: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) {
            Log.e("MoonShader", "$name: Failed to create shader")
            return 0
        }

        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            val log = GLES20.glGetShaderInfoLog(shader)
            Log.e("MoonShader", "$name compile error:\n$log")
            GLES20.glDeleteShader(shader)
            return 0
        }

        Log.i("MoonShader", "$name compiled successfully")
        return shader
    }

    private fun checkLinkStatus(program: Int, name: String) {
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] == 0) {
            val log = GLES20.glGetProgramInfoLog(program)
            Log.e("MoonShader", "$name link failed:\n$log")
            GLES20.glDeleteProgram(program)
            programId = 0
        } else {
            Log.i("MoonShader", "$name linked successfully")
        }
    }
}