package com.example.neigungsmesser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.neigungsmesser.sensor.SensorViewModel
import com.example.neigungsmesser.ui.InclinometerScreen
import com.example.neigungsmesser.ui.theme.NeigungsmesserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeigungsmesserTheme {
                val viewModel: SensorViewModel = viewModel(
                    factory = SensorViewModel.Factory(applicationContext)
                )
                InclinometerScreen(viewModel = viewModel)
            }
        }
    }
}
