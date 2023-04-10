package org.sagebionetworks.motorcontrol

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.motorcontrol_android.ContainerActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.sagebionetworks.assessmentmodel.presentation.AssessmentActivity

class TappingUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    private lateinit var currentActivity : AssessmentActivity
    private val measureId = "finger-tapping"
    private val uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId)
    private val exit = "Exit"
    private val startTheTest = "Start the test"
    private val tapLeft = "tap_left_1"
    private val flatSurface = "f_flat_surface"
    private val flatSurfaceIcon = "flat_surface"


    @Before
    fun navigateToHandSelection() {
        onView(ViewMatchers.withText(measureId))
            .perform(ViewActions.click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
        uiTestHelper.currentActivity = currentActivity
        uiTestHelper.assertAndClick("Get started", imageNames = listOf(tapLeft, flatSurfaceIcon))
        uiTestHelper.assertAndClick("Got it", imageNames = listOf(tapLeft))
    }

    @Test
    fun testTappingRightHand() {
        selectHand("RIGHT")
        navigateThroughInstructions()
        uiTestHelper.performTappingStep(exit, tapLeft)
    }

    @Test
    fun testTappingLeftHand() {
        selectHand("LEFT")
        navigateThroughInstructions()
        uiTestHelper.performTappingStep(exit, tapLeft)
    }

    @Test
    fun testTappingBothHands() {
        selectHand("BOTH")
        navigateThroughInstructions()
        uiTestHelper.performTappingStep(startTheTest, tapLeft)
        uiTestHelper.performTappingStep(exit, tapLeft)
    }

    private fun selectHand(hand: String) {
        uiTestHelper.assertAndClick(hand, true)
        uiTestHelper.assertAndClick("Next", true)
    }

    private fun navigateThroughInstructions() {
        uiTestHelper.assertAndClick("Did it", true, imageNames = listOf(flatSurface))
        uiTestHelper.assertAndClick(startTheTest, imageNames = listOf(tapLeft))
    }

}