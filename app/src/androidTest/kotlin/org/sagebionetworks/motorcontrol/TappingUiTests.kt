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
    private lateinit var uiTestHelper: MotorControlUITestHelper

    private val measureId = "finger-tapping"
    private val tapLeft = "tap_left_1"
    private val flatSurface = "f_flat_surface"
    private val flatSurfaceIcon = "flat_surface"

    @Before
    fun navigateToHandSelection() {
        onView(ViewMatchers.withText(measureId))
            .perform(ViewActions.click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
        uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId, currentActivity)
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.get_started), imageNames = listOf(tapLeft, flatSurfaceIcon))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.got_it), imageNames = listOf(tapLeft))
    }

    @Test
    fun testTappingRightHand() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.right))
        navigateThroughInstructions()
        uiTestHelper.performTappingStep(uiTestHelper.getString(R.string.exit), tapLeft)
    }

    @Test
    fun testTappingLeftHand() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.left))
        navigateThroughInstructions()
        uiTestHelper.performTappingStep(uiTestHelper.getString(R.string.exit), tapLeft)
    }

    @Test
    fun testTappingBothHands() {
        uiTestHelper.selectHand(uiTestHelper.getString(R.string.both))
        navigateThroughInstructions()
        uiTestHelper.performTappingStep(uiTestHelper.getString(R.string.start_test), tapLeft)
        uiTestHelper.performTappingStep(uiTestHelper.getString(R.string.exit), tapLeft)
    }

    private fun navigateThroughInstructions() {
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.did_it), true, imageNames = listOf(flatSurface))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.start_test), imageNames = listOf(tapLeft))
    }

}