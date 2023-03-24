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
import org.sagebionetworks.assessmentmodel.presentation.AssessmentActivity
import org.sagebionetworks.assessmentmodel.serialization.AssessmentResultObject
import org.sagebionetworks.assessmentmodel.serialization.BranchNodeResultObject

class WalkAndBalanceUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    lateinit var currentActivity : AssessmentActivity
    private val walkAndBalance = "walk-and-balance"
    private val gotIt = "Got it"
    private val exit = "Exit"

    @Before
    fun navigateThroughInstructions() {
        onView(withText("walk-and-balance"))
            .perform(click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
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
    fun testWalkAndBalance() {
        performWalkStep()
        performBalanceStep()
    }

    private fun performWalkStep() {
        composeTestRule.waitUntil(40000) {
            composeTestRule
                .onAllNodesWithText(gotIt)
                .fetchSemanticsNodes().size == 1
        }
        composeTestRule.onNodeWithText(gotIt)
            .performClick()
    }

    private fun performBalanceStep() {
        composeTestRule.onNodeWithText("Putting it in my pocket")
            .performClick()
        composeTestRule.waitUntil(40000) {
            composeTestRule
                .onAllNodesWithText(exit)
                .fetchSemanticsNodes().size == 1
        }

        // Test that the expected data was generated into currentResults
        val result = currentActivity.viewModel.assessmentNodeState?.currentResult
        result?.let { branchNodeResult ->
            val assessmentResult = branchNodeResult as AssessmentResultObject
            val activeSteps = result.pathHistoryResults.filter{
                    it -> it.identifier == "walk" || it.identifier == "balance"
            }
            assert(assessmentResult.assessmentIdentifier == walkAndBalance
                    && assessmentResult.identifier == walkAndBalance)
            assert(activeSteps.size == 2)
            activeSteps.forEach { walkOrBalanceStepResult ->
                val casted = walkOrBalanceStepResult as BranchNodeResultObject
                assert(casted.inputResults.size == 1)
            }
        }

        composeTestRule.onNodeWithText(exit)
            .performClick()
    }
}