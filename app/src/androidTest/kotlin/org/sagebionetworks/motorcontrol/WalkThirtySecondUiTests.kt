package org.sagebionetworks.motorcontrol

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.motorcontrol_android.ContainerActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WalkThirtySecondUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    private val exit = "Exit"

    @Before
    fun navigateThroughInstructions() {
        onView(withText("walk-thirty-second"))
            .perform(click())
        composeTestRule.onNodeWithText("Get started")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Got it")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("The phone’s volume is up")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Got front pockets")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Putting it in my pocket")
            .assertExists()
            .performClick()
    }

    @Test
    fun testWalkThirtySeconds() {
        performWalkStep()
    }

    private fun performWalkStep() {
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText(exit)
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(exit)
            .performClick()
    }

}