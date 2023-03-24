package org.sagebionetworks.motorcontrol

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.motorcontrol_android.ContainerActivity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.sagebionetworks.assessmentmodel.presentation.AssessmentActivity
import org.sagebionetworks.assessmentmodel.serialization.AnswerResultObject
import org.sagebionetworks.assessmentmodel.serialization.AssessmentResultObject
import org.sagebionetworks.assessmentmodel.serialization.BranchNodeResultObject

class TappingUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    lateinit var currentActivity : AssessmentActivity
    private val tapping = "finger-tapping"
    private val exit = "Exit"
    private val startTheTest = "Start the test"


    @Before
    fun navigateToHandSelection() {
        onView(ViewMatchers.withText("finger-tapping"))
            .perform(ViewActions.click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
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

        // Simulate random tapping
        val buttons = listOf(
            composeTestRule.onNodeWithTag("LEFT_BUTTON"),
            composeTestRule.onNodeWithTag("RIGHT_BUTTON"),
            composeTestRule.onNodeWithTag("TAP_SCREEN")
        )
        for (ii in 0..75) {
            buttons[(0..2).random()].performClick()
        }
        composeTestRule.waitUntil(40000) {
            composeTestRule
                .onAllNodesWithText(nextButtonToPress)
                .fetchSemanticsNodes().size == 1
        }

        // Test that the expected data was generated into currentResults
        if (nextButtonToPress == exit) {
            val result = currentActivity.viewModel.assessmentNodeState?.currentResult
            result?.let { branchNodeResult ->
                val assessmentResult = branchNodeResult as AssessmentResultObject
                val handSelectionStep = result.pathHistoryResults.filter { it ->
                    it.identifier == "handSelection"
                }[0] as AnswerResultObject
                val activeSteps = result.pathHistoryResults.filter{ it ->
                    it.identifier == "right" || it.identifier == "left"
                }
                // Assert that the identifiers are as expected
                assert(assessmentResult.assessmentIdentifier == tapping
                        && assessmentResult.identifier == tapping)
                val hand = handSelectionStep.jsonValue?.let {
                    Json.decodeFromJsonElement<String>(it)
                }
                // Assert the amount of active steps is accurate
                assert(activeSteps.size == if (hand == "both") 2 else 1)
                activeSteps.forEach { tappingStepResult ->
                    val casted = tappingStepResult as BranchNodeResultObject
                    // Assert that the results of each active step were recorded
                    assert(casted.inputResults.size == 1)
                }
            }
        }

        composeTestRule.onNodeWithText(nextButtonToPress)
            .performClick()
    }
}