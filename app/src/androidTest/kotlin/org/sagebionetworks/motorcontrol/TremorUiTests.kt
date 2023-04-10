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


class TremorUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    private val measureId = "tremor"
    private val uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId)
    private val holdPhone = "Hold phone"
    private val exit = "Exit"
    private val holdPhoneLeft = "hold_phone_left"
    private val tremorHoldPhone = "tremor_hold_phone"
    private val sitting = "sitting"
    private val comfortablePlaceToSit = "comfortable_place_to_sit"
    lateinit var currentActivity : AssessmentActivity

    @Before
    fun navigateToHandSelection() {
        onView(withText(measureId))
            .perform(click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
        uiTestHelper.currentActivity = currentActivity
        uiTestHelper.assertAndClick("Get started", imageNames = listOf(holdPhoneLeft, comfortablePlaceToSit))
    }

    @Test
    fun testTremorRightHand() {
        selectHand("RIGHT")
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(exit, holdPhoneLeft)
    }

    @Test
    fun testTremorLeftHand() {
        selectHand("LEFT")
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(exit, holdPhoneLeft)
    }

    @Test
    fun testTremorBothHands() {
        selectHand("BOTH")
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(holdPhone, holdPhoneLeft)
        uiTestHelper.performMotionStep(exit, holdPhoneLeft)
    }

    private fun selectHand(hand: String) {
        uiTestHelper.assertAndClick(hand, true)
        uiTestHelper.assertAndClick("Next", true)
    }

    private fun navigateThroughInstructions() {
        uiTestHelper.assertAndClick("Got it", imageNames = listOf(tremorHoldPhone))
        uiTestHelper.assertAndClick("Got a spot", imageNames = listOf(sitting))
        uiTestHelper.assertAndClick(holdPhone, imageNames = listOf(holdPhoneLeft))
    }
}