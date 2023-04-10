package org.sagebionetworks.motorcontrol

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.motorcontrol_android.ContainerActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.sagebionetworks.assessmentmodel.presentation.AssessmentActivity

class KineticTremorUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    lateinit var currentActivity : AssessmentActivity
    private val measureId = "kinetic-tremor"
    private val uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId)
    private val exit = "Exit"
    private val start = "Start"
    private val gotIt = "Got it"
    private val holdPhoneLeft = "kinetic_hold_phone_left"
    private val fingerToNose1 = "finger_to_nose_1"
    private val fingerToNose2 = "finger_to_nose_2"
    private val spaceToMoveArms = "space_to_move_your_arms"
    private val placeToSit = "comfortable_place_to_sit"

    @Before
    fun navigateToHandSelection() {
        onView(withText(measureId))
            .perform(click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
        uiTestHelper.currentActivity = currentActivity
        uiTestHelper.assertAndClick("Get started", imageNames = listOf(holdPhoneLeft, spaceToMoveArms, placeToSit))
        uiTestHelper.assertAndClick(gotIt, imageNames = listOf(holdPhoneLeft))
    }

    @Test
    fun testKineticTremorRightHand() {
        selectHand("RIGHT")
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(exit, holdPhoneLeft)
    }

    @Test
    fun testKineticTremorLeftHand() {
        selectHand("LEFT")
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(exit, holdPhoneLeft)
    }

    @Test
    fun testKineticTremorBothHands() {
        selectHand("BOTH")
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(start, holdPhoneLeft)
        uiTestHelper.performMotionStep(exit, holdPhoneLeft)
    }

    private fun selectHand(hand: String) {
        uiTestHelper.assertAndClick(hand, true)
        uiTestHelper.assertAndClick("Next", true)
    }

    private fun navigateThroughInstructions() {
        uiTestHelper.assertAndClick("Got a spot", imageNames = listOf(fingerToNose1))
        uiTestHelper.assertAndClick("Pointing index finger", imageNames = listOf(fingerToNose2))
        uiTestHelper.assertAndClick(gotIt, imageNames = listOf(holdPhoneLeft))
        uiTestHelper.assertAndClick(start, imageNames = listOf(holdPhoneLeft))
    }

}