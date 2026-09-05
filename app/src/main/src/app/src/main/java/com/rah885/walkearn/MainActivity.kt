package com.rah885.walkearn

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

class MainActivity : Activity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private lateinit var stepsText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var statusText: TextView
    private lateinit var coinsText: TextView

    private var baseSteps = -1f

    private val dailyGoal = 10000

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

        if (
            Build.VERSION.SDK_INT >= 29 &&
            checkSelfPermission(
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                100
            )
        }
    }

    private fun createScreen() {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.rgb(248, 249, 251))
        root.setPadding(24, 32, 24, 24)

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL

        val titleLayout = LinearLayout(this)
        titleLayout.orientation = LinearLayout.VERTICAL

        val title = TextView(this)
        title.text = "WalkEarn"
        title.textSize = 28f
        title.setTextColor(Color.rgb(25, 25, 25))

        val subtitle = TextView(this)
        subtitle.text = "Walk • Track • Earn"
        subtitle.textSize = 14f
        subtitle.setTextColor(Color.DKGRAY)

        titleLayout.addView(title)
        titleLayout.addView(subtitle)

        header.addView(
            titleLayout,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        coinsText = TextView(this)
        coinsText.text = "🪙 0"
        coinsText.textSize = 18f
        coinsText.setTextColor(Color.rgb(25, 25, 25))
        coinsText.gravity = Gravity.CENTER

        val coinsBackground = GradientDrawable()
        coinsBackground.setColor(Color.WHITE)
        coinsBackground.cornerRadius = 40f
        coinsBackground.setStroke(1, Color.LTGRAY)
        coinsText.background = coinsBackground
        coinsText.setPadding(20, 12, 20, 12)

        header.addView(
            coinsText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(header)

        addSpace(root, 28)

        val activityTitle = TextView(this)
        activityTitle.text = "Today's Activity"
        activityTitle.textSize = 20f
        activityTitle.setTextColor(Color.rgb(30, 30, 30))
        root.addView(activityTitle)

        addSpace(root, 12)

        val stepsCard = LinearLayout(this)
        stepsCard.orientation = LinearLayout.VERTICAL
        stepsCard.gravity = Gravity.CENTER
        stepsCard.setPadding(20, 28, 20, 28)

        val cardBackground = GradientDrawable()
        cardBackground.setColor(Color.WHITE)
        cardBackground.cornerRadius = 28f
        cardBackground.setStroke(1, Color.rgb(230, 230, 230))
        stepsCard.background = cardBackground

        val stepsLabel = TextView(this)
        stepsLabel.text = "STEPS"
        stepsLabel.textSize = 13f
        stepsLabel.setTextColor(Color.GRAY)
        stepsLabel.gravity = Gravity.CENTER

        stepsText = TextView(this)
        stepsText.text = "0"
        stepsText.textSize = 58f
        stepsText.setTextColor(Color.rgb(20, 20, 20))
        stepsText.gravity = Gravity.CENTER

        val todayLabel = TextView(this)
        todayLabel.text = "Today's Steps"
        todayLabel.textSize = 16f
        todayLabel.setTextColor(Color.DKGRAY)
        todayLabel.gravity = Gravity.CENTER

        stepsCard.addView(stepsLabel)
        stepsCard.addView(stepsText)
        stepsCard.addView(todayLabel)

        root.addView(
            stepsCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(root, 18)

        val goalRow = LinearLayout(this)
        goalRow.orientation = LinearLayout.HORIZONTAL
        goalRow.gravity = Gravity.CENTER_VERTICAL

        val goalTitle = TextView(this)
        goalTitle.text = "Daily Goal"
        goalTitle.textSize = 17f
        goalTitle.setTextColor(Color.rgb(35, 35, 35))

        goalRow.addView(
            goalTitle,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        progressText = TextView(this)
        progressText.text = "0 / 10,000"
        progressText.textSize = 15f
        progressText.setTextColor(Color.DKGRAY)

        goalRow.addView(progressText)

        root.addView(goalRow)

        addSpace(root, 10)

        progressBar = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        )

        progressBar.max = dailyGoal
        progressBar.progress = 0

        root.addView(
            progressBar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                14
            )
        )

        addSpace(root, 20)

        val earningText = TextView(this)
        earningText.text = "Keep moving, keep earning!"
        earningText.textSize = 17f
        earningText.setTextColor(Color.rgb(45, 45, 45))
        earningText.gravity = Gravity.CENTER

        root.addView(
            earningText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(root, 14)

        statusText = TextView(this)
        statusText.text = "Starting..."
        statusText.textSize = 14f
        statusText.setTextColor(Color.GRAY)
        statusText.gravity = Gravity.CENTER

        root.addView(
            statusText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(root, 18)

        val resetButton = Button(this)
        resetButton.text = "Reset Today"
        resetButton.textSize = 15f
        resetButton.setAllCaps(false)

        resetButton.setOnClickListener {
            baseSteps = -1f
            stepsText.text = "0"
            progressText.text = "0 / 10,000"
            progressBar.progress = 0
            coinsText.text = "🪙 0"
        }

        root.addView(
            resetButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            View(this),
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val footer = TextView(this)
        footer.text = "Powered by your phone's hardware step sensor"
        footer.textSize = 12f
        footer.setTextColor(Color.GRAY)
        footer.gravity = Gravity.CENTER

        root.addView(
            footer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)
    }

    private fun addSpace(layout: LinearLayout, height: Int) {
        val space = View(this)
        layout.addView(
            space,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            )
        )
    }

    override fun onResume() {
        super.onResume()

        stepSensor?.let {
            sensorManager.registerListener(
                this,
                it,
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

            val todaySteps =
                (totalSteps - baseSteps)
                    .toInt()
                    .coerceAtLeast(0)

            stepsText.text = todaySteps.toString()

            val progress =
                todaySteps.coerceAtMost(dailyGoal)

            progressBar.progress = progress

            progressText.text =
                "$todaySteps / 10,000"

            val earnedCoins =
                todaySteps / 100

            coinsText.text =
                "🪙 $earnedCoins"
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}
