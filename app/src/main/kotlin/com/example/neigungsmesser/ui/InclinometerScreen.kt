package com.example.neigungsmesser.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neigungsmesser.R
import com.example.neigungsmesser.sensor.SensorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InclinometerScreen(viewModel: SensorViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val bubbleColor = if (state.isLevel) Color(0xFF4CAF50) else Color(0xFFF44336)

    val animatedRoll by animateFloatAsState(
        targetValue = state.rollDegrees,
        animationSpec = tween(durationMillis = 80),
        label = "roll"
    )
    val animatedPitch by animateFloatAsState(
        targetValue = state.pitchDegrees,
        animationSpec = tween(durationMillis = 80),
        label = "pitch"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = if (state.isLevel) stringResource(R.string.status_level)
                       else stringResource(R.string.status_tilted),
                style = MaterialTheme.typography.titleMedium,
                color = bubbleColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("status_text")
            )

            BubbleLevelCanvas(
                rollDegrees = animatedRoll,
                pitchDegrees = animatedPitch,
                bubbleColor = bubbleColor,
                modifier = Modifier
                    .size(280.dp)
                    .testTag("bubble_canvas")
            )

            AngleReadings(
                rollDegrees = state.rollDegrees,
                pitchDegrees = state.pitchDegrees,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.calibrate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calibrate_button")
            ) {
                Text(
                    text = stringResource(R.string.button_calibrate),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun BubbleLevelCanvas(
    rollDegrees: Float,
    pitchDegrees: Float,
    bubbleColor: Color,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.minDimension / 2f - 8.dp.toPx()
        val bubbleRadius = outerRadius * 0.18f

        val maxOffset = outerRadius - bubbleRadius

        val clampFactor = 45f
        val rawX = (rollDegrees / clampFactor) * maxOffset
        val rawY = (-pitchDegrees / clampFactor) * maxOffset
        val dist = kotlin.math.sqrt((rawX * rawX + rawY * rawY).toDouble()).toFloat()
        val scale = if (dist > maxOffset) maxOffset / dist else 1f
        val bubbleOffset = Offset(rawX * scale, rawY * scale)

        drawCircle(color = surfaceColor, radius = outerRadius, center = center)
        drawCircle(color = outlineColor, radius = outerRadius, center = center, style = Stroke(width = 2.dp.toPx()))

        drawLine(
            outlineColor.copy(alpha = 0.4f),
            Offset(center.x - outerRadius, center.y),
            Offset(center.x + outerRadius, center.y),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            outlineColor.copy(alpha = 0.4f),
            Offset(center.x, center.y - outerRadius),
            Offset(center.x, center.y + outerRadius),
            strokeWidth = 1.dp.toPx()
        )

        drawCircle(
            color = outlineColor.copy(alpha = 0.3f),
            radius = bubbleRadius * 1.3f,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )

        drawCircle(
            color = bubbleColor.copy(alpha = 0.85f),
            radius = bubbleRadius,
            center = center + bubbleOffset
        )
        drawCircle(
            color = bubbleColor,
            radius = bubbleRadius,
            center = center + bubbleOffset,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun AngleReadings(
    rollDegrees: Float,
    pitchDegrees: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AngleCard(
            label = "Roll (X)",
            value = rollDegrees,
            modifier = Modifier.testTag("roll_value")
        )
        AngleCard(
            label = "Pitch (Y)",
            value = pitchDegrees,
            modifier = Modifier.testTag("pitch_value")
        )
    }
}

@Composable
private fun AngleCard(
    label: String,
    value: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "%.1f°".format(value),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}
