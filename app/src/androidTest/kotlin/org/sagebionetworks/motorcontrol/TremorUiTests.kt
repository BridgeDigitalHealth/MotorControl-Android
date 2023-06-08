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
    private lateinit var currentActivity : AssessmentActivity
    private lateinit var uiTestHelper: MotorControlUITestHelper

    private val measureId = "tremor"
    private val holdPhoneLeft = "hold_phone_left"
    private val tremorHoldPhone = "tremor_hold_phone"
    private val sitting = "sitting"
    private val comfortablePlaceToSit = "comfortable_place_to_sit"

    @Before
    fun navigateToHandSelection() {
        onView(withText(measureId))
            .perform(click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
        uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId, currentActivity)
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.get_started), imageNames = listOf(holdPhoneLeft, comfortablePlaceToSit))
    }

    @Test
    fun testTremorRightHand() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.right))
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.exit), holdPhoneLeft)
    }

    @Test
    fun testTremorLeftHand() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.left))
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.exit), holdPhoneLeft)
    }

    @Test
    fun testTremorBothHands() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.both))
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.hold_phone), holdPhoneLeft)
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.exit), holdPhoneLeft)
    }

    private fun navigateThroughInstructions() {
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.got_it), imageNames = listOf(tremorHoldPhone))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.got_a_spot), imageNames = listOf(sitting))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.hold_phone), imageNames = listOf(holdPhoneLeft))
    }
}