package com.example.neigungsmesser

import com.example.neigungsmesser.sensor.InclinationState
import org.junit.Test
import org.junit.Assert.*

class SensorViewModelTest {

    @Test
    fun `initial state is level`() {
        val state = InclinationState()
        assertEquals(0f, state.rollDegrees)
        assertEquals(0f, state.pitchDegrees)
        assertTrue(state.isLevel)
    }

    @Test
    fun `tilted state has isLevel false`() {
        val state = InclinationState(rollDegrees = 10f, pitchDegrees = 5f, isLevel = false)
        assertFalse(state.isLevel)
    }

    @Test
    fun `small angles within threshold are level`() {
        val state = InclinationState(rollDegrees = 1f, pitchDegrees = 1f, isLevel = true)
        assertTrue(state.isLevel)
    }

    @Test
    fun `angle string formats correctly`() {
        val value = 12.345f
        val formatted = "%.1f°".format(value)
        assertEquals("12.3°", formatted)
    }
}
