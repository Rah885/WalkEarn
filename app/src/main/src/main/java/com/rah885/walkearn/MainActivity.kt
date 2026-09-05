package com.rah885.walkearn

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private lateinit var stepsText: TextView
    private lateinit var statusText: TextView

    private var baseSteps = -1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createScreen()

        sensorManager =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager

        stepSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            statusText.text = "Step Counter sensor not available"
        } else {
            statusText.text = "Hardware Step Counter ready"
        }

        if (Build.VERSION.SDK_INT >= 29 &&
            checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                100
            )
        }
    }

    private fun createScreen() {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)

        val title = TextView(this)
        title.text = "WalkEarn"
        title.textSize = 32f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER
        layout.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Walk • Track • Earn"
        subtitle.textSize = 18f
        subtitle.gravity = Gravity.CENTER
        layout.addView(subtitle)

        stepsText = TextView(this)
        stepsText.text = "0"
        stepsText.textSize = 60f
        stepsText.gravity = Gravity.CENTER
        stepsText.setPadding(0, 40, 0, 40)
        layout.addView(stepsText)

        val label = TextView(this)
        label.text = "Today's Steps"
        label.textSize = 20f
        label.gravity = Gravity.CENTER
        layout.addView(label)

        statusText = TextView(this)
        statusText.text = "Starting..."
        statusText.textSize = 16f
        statusText.gravity = Gravity.CENTER
        statusText.setPadding(0, 30, 0, 30)
        layout.addView(statusText)

        val resetButton = Button(this)
        resetButton.text = "Reset Today"

        resetButton.setOnClickListener {
            baseSteps = -1f
            stepsText.text = "0"
        }

        layout.addView(resetButton)

        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()

        if (stepSensor != null) {
            sensorManager.registerListener(
                this,
                stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {

            val totalSteps = event.values[0]

            if (baseSteps < 0) {
                baseSteps = totalSteps
            }

            val todaySteps = totalSteps - baseSteps

            stepsText.text =
                todaySteps.toInt().coerceAtLeast(0).toString()
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}
