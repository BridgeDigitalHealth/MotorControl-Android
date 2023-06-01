package org.sagebionetworks.motorcontrol

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.motorcontrol_android.ContainerActivity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.sagebionetworks.assessmentmodel.presentation.AssessmentActivity
import org.sagebionetworks.assessmentmodel.serialization.AnswerResultObject
import org.sagebionetworks.assessmentmodel.serialization.AssessmentResultObject
import org.sagebionetworks.assessmentmodel.serialization.BranchNodeResultObject

class MotorControlUITestHelper(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ContainerActivity>, ContainerActivity>,
    private val measureId: String
) {
    lateinit var currentActivity: AssessmentActivity
    private val exit = "Exit"


    fun assertAndClick(buttonText: String,
                       isSubstring: Boolean = false,
                       imageNames: List<String> = listOf()) {
        assertImages(imageNames)
        composeTestRule.onNodeWithText(buttonText, substring = isSubstring)
            .assertExists()
            .performClick()
    }


    fun assertImages(imageNames: List<String>) {
        for (imageName in imageNames) {
            composeTestRule.onNodeWithTag(imageName)
                .assertExists()
        }
    }


    fun performMotionStep(nextButtonToPress: String, currentImage: String, isTwoHand: Boolean = true) {
        composeTestRule.waitUntil(6000) {
            composeTestRule
                .onAllNodesWithTag(currentImage)
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.waitUntil(40000) {
            composeTestRule
                .onAllNodesWithText(nextButtonToPress)
                .fetchSemanticsNodes().size == 1
        }
        if (nextButtonToPress == exit) {
            checkCurrentResults(isTwoHand)
        }
        assertAndClick(nextButtonToPress)
    }


    fun performTappingStep(nextButtonToPress: String, currentImage: String) {
        assertImages(listOf(currentImage))
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
            checkCurrentResults()
        }
        assertAndClick(nextButtonToPress)
    }


    private fun checkCurrentResults(isTwoHand: Boolean = true) {
        // Test that the expected data was generated into currentResults
        val result = currentActivity.viewModel.assessmentNodeState?.currentResult
        result?.let { branchNodeResult ->
            val assessmentResult = branchNodeResult as AssessmentResultObject
            if (isTwoHand) {
                twoHandAssertions(assessmentResult)
            } else {
                walkAndBalanceAssertions(assessmentResult)
            }
        }
    }


    private fun twoHandAssertions(assessmentResult: AssessmentResultObject) {
        val handSelectionStep = assessmentResult.pathHistoryResults.filter {
            it.identifier == "handSelection"
        }[0] as AnswerResultObject
        val activeSteps = assessmentResult.pathHistoryResults.filter{
            it.identifier == "right" || it.identifier == "left"
        }

        // Assert that the identifiers are as expected
        assert(assessmentResult.assessmentIdentifier == measureId
                && assessmentResult.identifier == measureId)
        val hand = handSelectionStep.jsonValue?.let {
            Json.decodeFromJsonElement<String>(it)
        }
        // Assert the amount of active steps is accurate
        assert(activeSteps.size == if (hand == "both") 2 else 1)
        activeSteps.forEach { motionStepResult ->
            val casted = motionStepResult as BranchNodeResultObject
            // Assert that the results of each active step were recorded
            assert(casted.inputResults.size == 1)
        }
    }


    private fun walkAndBalanceAssertions(assessmentResult: AssessmentResultObject) {
        val activeSteps = assessmentResult.pathHistoryResults.filter{
            it.identifier == "walk" || it.identifier == "balance"
        }
        assert(assessmentResult.assessmentIdentifier == measureId
                && assessmentResult.identifier == measureId)
        assert(activeSteps.size == (if (measureId == "walk-thirty-second") 1 else 2))
        activeSteps.forEach { walkStepResult ->
            val casted = walkStepResult as BranchNodeResultObject
            assert(casted.inputResults.size == 1)
        }
    }

}