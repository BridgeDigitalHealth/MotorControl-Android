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
    private lateinit var currentActivity : AssessmentActivity
    private lateinit var uiTestHelper: MotorControlUITestHelper

    private val measureId = "kinetic-tremor"
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
        uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId, currentActivity)
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.get_started), imageNames = listOf(holdPhoneLeft, spaceToMoveArms, placeToSit))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.got_it), imageNames = listOf(holdPhoneLeft))
    }

    @Test
    fun testKineticTremorRightHand() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.right))
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.exit), holdPhoneLeft)
    }

    @Test
    fun testKineticTremorLeftHand() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.left))
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.exit), holdPhoneLeft)
    }

    @Test
    fun testKineticTremorBothHands() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.both))
        navigateThroughInstructions()
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.start), holdPhoneLeft)
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.exit), holdPhoneLeft)
    }

    private fun navigateThroughInstructions() {
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.got_a_spot), imageNames = listOf(fingerToNose1))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.pointing), imageNames = listOf(fingerToNose2))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.got_it), imageNames = listOf(holdPhoneLeft))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.start), imageNames = listOf(holdPhoneLeft))
    }

}