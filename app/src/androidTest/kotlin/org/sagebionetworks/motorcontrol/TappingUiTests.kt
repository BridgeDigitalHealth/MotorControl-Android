package org.sagebionetworks.motorcontrol

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.motorcontrol_android.ContainerActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TappingUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    private val exit = "Exit"
    private val startTheTest = "Start the test"

    @Before
    fun navigateToHandSelection() {
        onView(ViewMatchers.withText("finger-tapping"))
            .perform(ViewActions.click())
        composeTestRule.onNodeWithText("Get started")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Got it")
            .assertExists()
            .performClick()
    }

    @Test
    fun testTappingRightHand() {
        selectHand("RIGHT")
        navigateThroughInstructions()
        performTappingStep(exit)
    }

    @Test
    fun testTappingLeftHand() {
        selectHand("LEFT")
        navigateThroughInstructions()
        performTappingStep(exit)
    }

    @Test
    fun testTappingBothHands() {
        selectHand("BOTH")
        navigateThroughInstructions()
        performTappingStep(startTheTest)
        performTappingStep(exit)
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
        composeTestRule.onNodeWithText("Did it", substring = true)
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText(startTheTest)
            .assertExists()
            .performClick()
    }

    private fun performTappingStep(nextButtonToPress: String) {

        composeTestRule.waitUntil(1000) {
            composeTestRule
                .onAllNodesWithText("Tap")
                .fetchSemanticsNodes().size == 2
        }

        // Unable to test rapid tapping due to recomposition caused by background
        // timer for the CountdownDial. ComposeTestRule waits for app to be idle
        // before executing next line in test code arabara 3/20/23
        val leftButton = composeTestRule.onNodeWithTag("LEFT_BUTTON")
        leftButton.performClick()

        composeTestRule.waitUntil(40000) {
            composeTestRule
                .onAllNodesWithText(nextButtonToPress)
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(nextButtonToPress)
            .performClick()
    }
}