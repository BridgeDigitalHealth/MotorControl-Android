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

class TremorUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    private val exit = "Exit"

    @Before
    fun navigateToHandSelection() {
        onView(withText("tremor"))
            .perform(click())
        composeTestRule.onNodeWithText("Get started")
            .assertExists()
            .performClick()
    }

    @Test
    fun testTremorRightHand() {
        selectHand("RIGHT")
        navigateThroughInstructions()
        performTremorStep(exit)
    }

    @Test
    fun testTremorLeftHand() {
        selectHand("LEFT")
        navigateThroughInstructions()
        performTremorStep(exit)
    }

    @Test
    fun testTremorBothHands() {
        selectHand("BOTH")
        navigateThroughInstructions()
        performTremorStep("Hold phone")
        performTremorStep(exit)
    }

    private fun selectHand(hand: String) {
        composeTestRule.onNodeWithText(hand, substring = true)
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Next", substring = true)
            .assertExists()
            .performClick()
    }

    private fun navigateThroughInstructions() {
        composeTestRule.onNodeWithText("Got it")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Got a spot")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Hold phone")
            .assertExists()
            .performClick()
    }

    private fun performTremorStep(nextButtonToPress: String) {
        composeTestRule.waitUntil(10000) {
            composeTestRule
                .onAllNodesWithText(nextButtonToPress)
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(nextButtonToPress)
            .performClick()
    }

}