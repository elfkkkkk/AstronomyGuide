package com.example.astronomyguide.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.astronomyguide.data.models.SolarSystemData
import com.example.astronomyguide.data.models.Planet
import com.example.astronomyguide.opengl.Sphere
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var square: Square
    private lateinit var sphere: Sphere
    private lateinit var selectionCube: Cube

    private val planets = SolarSystemData.bodies
    private var selectedIndex = 0

    private var globalRotation = 0f
    private val planetOrbitAngles = FloatArray(10) { 0f }
    private var moonOrbitAngle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        square = Square(context)
        sphere = Sphere()
        selectionCube = Cube()

        for (i in planetOrbitAngles.indices) {
            planetOrbitAngles[i] = (i * 45).toFloat()
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height

        // ============ ИЗМЕНЕНО ============
        // Приближаем проекцию
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 20f)

        // Камера ближе и выше
        Matrix.setLookAtM(viewMatrix, 0,
            0f, 2f, 6f,     // x, y, z позиция камеры
            0f, 0f, 0f,     // точка, куда смотрим
            0f, 1f, 0f      // вектор "вверх"
        )
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        globalRotation += 0.5f
        if (globalRotation > 360) globalRotation = 0f

        for (i in 1 until planets.size) {
            if (i != 4) {
                planetOrbitAngles[i] += planets[i].orbitSpeed
                if (planetOrbitAngles[i] > 360) planetOrbitAngles[i] -= 360f
            }
        }

        moonOrbitAngle += SolarSystemData.MOON_ORBIT_SPEED
        if (moonOrbitAngle > 360) moonOrbitAngle -= 360f

        drawBackground()
        drawSolarSystem()
        drawSelectionCube()
    }

    private fun drawBackground() {
        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.translateM(modelMatrix, 0, 0f, -3f, -3f)

        val scaleX = 20f
        val scaleY = 20f
        Matrix.scaleM(modelMatrix, 0, scaleX, scaleY, 1f)

        square.draw(projectionMatrix, viewMatrix, modelMatrix)
    }

    private fun drawSolarSystem() {
        val systemScale = 0.7f
        val planetSizeMultiplier = 1.5f
        val distanceMultiplier = 0.7f

//        val systemZ = 0

        // 1. Солнце
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, globalRotation, 0f, 1f, 0f)
        Matrix.scaleM(modelMatrix, 0, systemScale, systemScale, systemScale)

        val sun = planets[0]
        Matrix.scaleM(modelMatrix, 0, sun.radius * 2f, sun.radius * 2f, sun.radius * 2f)

        sphere.draw(projectionMatrix, viewMatrix, modelMatrix,
            floatArrayOf(sun.colorRed, sun.colorGreen, sun.colorBlue, 1.0f))

        // планеты
        for (i in 1 until planets.size) {
            if (i != 4) {
                val planet = planets[i]

                Matrix.setIdentityM(modelMatrix, 0)
                Matrix.rotateM(modelMatrix, 0, globalRotation, 0f, 1f, 0f)
                Matrix.scaleM(modelMatrix, 0, systemScale, systemScale, systemScale)
                Matrix.rotateM(modelMatrix, 0, planetOrbitAngles[i], 0f, 1f, 0f)
                Matrix.translateM(modelMatrix, 0, planet.distanceFromSun * distanceMultiplier, 0f, 0f)
                Matrix.rotateM(modelMatrix, 0, planetOrbitAngles[i] * 2, 0f, 1f, 0f)
                Matrix.scaleM(modelMatrix, 0, planet.radius * planetSizeMultiplier,
                    planet.radius * planetSizeMultiplier, planet.radius * planetSizeMultiplier)

                sphere.draw(projectionMatrix, viewMatrix, modelMatrix,
                    floatArrayOf(planet.colorRed, planet.colorGreen, planet.colorBlue, 1.0f))
            }
        }

        // луна
        val earth = SolarSystemData.earth
        val earthIndex = 3

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, globalRotation, 0f, 1f, 0f)
        Matrix.scaleM(modelMatrix, 0, systemScale, systemScale, systemScale)
        Matrix.rotateM(modelMatrix, 0, planetOrbitAngles[earthIndex], 0f, 1f, 0f)
        Matrix.translateM(modelMatrix, 0, earth.distanceFromSun * distanceMultiplier, 0f, 0f)

        // орбита Луны
        Matrix.rotateM(modelMatrix, 0, 90f, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, moonOrbitAngle, 0f, 1f, 0f)
        Matrix.translateM(modelMatrix, 0, SolarSystemData.MOON_DISTANCE * 0.6f, 0f, 0f)

        val moon = planets[4]
        Matrix.scaleM(modelMatrix, 0, moon.radius * planetSizeMultiplier * 1.2f,
            moon.radius * planetSizeMultiplier * 1.2f, moon.radius * planetSizeMultiplier * 1.2f)

        sphere.draw(projectionMatrix, viewMatrix, modelMatrix,
            floatArrayOf(moon.colorRed, moon.colorGreen, moon.colorBlue, 1.0f))
    }

    private fun drawSelectionCube() {
        val selected = planets[selectedIndex]

        val systemScale = 0.7f
        val distanceMultiplier = 0.7f

        Matrix.setIdentityM(modelMatrix, 0)

        // 1. вращение системы
        Matrix.rotateM(modelMatrix, 0, globalRotation, 0f, 1f, 0f)

        // 2. Масштаб всей системы
        Matrix.scaleM(modelMatrix, 0, systemScale, systemScale, systemScale)

        when (selectedIndex) {
            0 -> { // Солнце - в центре
                Matrix.scaleM(modelMatrix, 0,
                    selected.radius * 3f,
                    selected.radius * 3f,
                    selected.radius * 3f
                )
            }

            4 -> { // Луна - особый случай
                val earth = SolarSystemData.earth
                val earthIndex = 3

                // Позиция Земли
                Matrix.rotateM(modelMatrix, 0, planetOrbitAngles[earthIndex], 0f, 1f, 0f)
                Matrix.translateM(modelMatrix, 0,
                    earth.distanceFromSun * distanceMultiplier,
                    0f,
                    0f
                )

                // Орбита Луны (перпендикулярно)
                Matrix.rotateM(modelMatrix, 0, 90f, 1f, 0f, 0f)
                Matrix.rotateM(modelMatrix, 0, moonOrbitAngle, 0f, 1f, 0f)
                Matrix.translateM(modelMatrix, 0,
                    SolarSystemData.MOON_DISTANCE * 0.8f,
                    0f,
                    0f
                )

                // Масштаб куба вокруг Луны
                Matrix.scaleM(modelMatrix, 0,
                    selected.radius * 4f,
                    selected.radius * 4f,
                    selected.radius * 4f
                )
            }

            else -> { // Все остальные планеты
                // Вращение орбиты
                Matrix.rotateM(modelMatrix, 0, planetOrbitAngles[selectedIndex], 0f, 1f, 0f)

                // Расстояние от Солнца
                Matrix.translateM(modelMatrix, 0,
                    selected.distanceFromSun * distanceMultiplier,
                    0f,
                    0f
                )

                // Масштаб куба вокруг планеты
                Matrix.scaleM(modelMatrix, 0,
                    selected.radius * 4f,
                    selected.radius * 4f,
                    selected.radius * 4f
                )
            }
        }

        selectionCube.draw(projectionMatrix, viewMatrix, modelMatrix)
    }

    fun selectNext() {
        selectedIndex = (selectedIndex + 1) % planets.size
    }

    fun selectPrevious() {
        selectedIndex = (selectedIndex - 1 + planets.size) % planets.size
    }

    fun getSelectedPlanetInfo(): String {
        return planets[selectedIndex].description
    }
}