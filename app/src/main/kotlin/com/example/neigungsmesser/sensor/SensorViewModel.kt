package com.example.neigungsmesser.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.atan2
import kotlin.math.sqrt

data class InclinationState(
    val rollDegrees: Float = 0f,
    val pitchDegrees: Float = 0f,
    val isLevel: Boolean = true
)

private const val LEVEL_THRESHOLD_DEGREES = 2f
private const val LOW_PASS_ALPHA = 0.15f

class SensorViewModel(context: Context) : ViewModel(), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _state = MutableStateFlow(InclinationState())
    val state: StateFlow<InclinationState> = _state.asStateFlow()

    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredZ = 9.81f

    private var offsetRoll = 0f
    private var offsetPitch = 0f

    init {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        // Low-pass filter to smooth sensor values
        filteredX += LOW_PASS_ALPHA * (event.values[0] - filteredX)
        filteredY += LOW_PASS_ALPHA * (event.values[1] - filteredY)
        filteredZ += LOW_PASS_ALPHA * (event.values[2] - filteredZ)

        val roll = Math.toDegrees(
            atan2(filteredY.toDouble(), sqrt((filteredX * filteredX + filteredZ * filteredZ).toDouble()))
        ).toFloat() - offsetRoll

        val pitch = Math.toDegrees(
            atan2((-filteredX).toDouble(), sqrt((filteredY * filteredY + filteredZ * filteredZ).toDouble()))
        ).toFloat() - offsetPitch

        val isLevel = kotlin.math.abs(roll) < LEVEL_THRESHOLD_DEGREES &&
                kotlin.math.abs(pitch) < LEVEL_THRESHOLD_DEGREES

        _state.update { InclinationState(rollDegrees = roll, pitchDegrees = pitch, isLevel = isLevel) }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    fun calibrate() {
        offsetRoll += _state.value.rollDegrees
        offsetPitch += _state.value.pitchDegrees
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SensorViewModel(context) as T
    }
}
