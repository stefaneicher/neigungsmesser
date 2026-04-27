package com.example.neigungsmesser

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import com.example.neigungsmesser.ui.theme.NeigungsmesserTheme
import org.junit.Rule
import org.junit.Test

class InclinometerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun themeRendersWithoutCrash() {
        composeTestRule.setContent {
            NeigungsmesserTheme {
                // Minimal render test — verifies the theme and Compose rule work
            }
        }
    }
}
