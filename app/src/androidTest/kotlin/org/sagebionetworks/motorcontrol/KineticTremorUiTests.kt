package org.sagebionetworks.motorcontrol

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
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

class KineticTremorUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    lateinit var currentActivity : AssessmentActivity
    private val kineticTremor = "kinetic-tremor"
    private val exit = "Exit"
    private val start = "Start"
    private val gotIt = "Got it"

    @Before
    fun navigateToHandSelection() {
        onView(withText("kinetic-tremor"))
            .perform(click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
        composeTestRule.onNodeWithText("Get started")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText(gotIt)
            .assertExists()
            .performClick()
    }

    @Test
    fun testKineticTremorRightHand() {
        selectHand("RIGHT")
        navigateThroughInstructions()
        performKineticTremorStep(exit)
    }

    @Test
    fun testKineticTremorLeftHand() {
        selectHand("LEFT")
        navigateThroughInstructions()
        performKineticTremorStep(exit)
    }

    @Test
    fun testKineticTremorBothHands() {
        selectHand("BOTH")
        navigateThroughInstructions()
        performKineticTremorStep(start)
        performKineticTremorStep(exit)
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
        composeTestRule.onNodeWithText("Got a spot")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText("Pointing index finger")
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText(gotIt)
            .assertExists()
            .performClick()
        composeTestRule.onNodeWithText(start)
            .assertExists()
            .performClick()
    }

    private fun performKineticTremorStep(nextButtonToPress: String) {
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
                val activeSteps = result.pathHistoryResults.filter{
                        it -> it.identifier == "right" || it.identifier == "left"
                }
                // Assert that the identifiers are as expected
                assert(assessmentResult.assessmentIdentifier == kineticTremor
                        && assessmentResult.identifier == kineticTremor)
                val hand = handSelectionStep.jsonValue?.let {
                    Json.decodeFromJsonElement<String>(it)
                }
                // Assert the amount of active steps is accurate
                assert(activeSteps.size == if (hand == "both") 2 else 1)
                activeSteps.forEach { tremorStepResult ->
                    val casted = tremorStepResult as BranchNodeResultObject
                    // Assert that the results of each active step were recorded
                    assert(casted.inputResults.size == 1)
                }
            }
        }

        composeTestRule.onNodeWithText(nextButtonToPress)
            .performClick()
    }
}