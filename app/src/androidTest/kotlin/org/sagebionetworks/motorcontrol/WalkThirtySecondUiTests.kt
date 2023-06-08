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

class WalkThirtySecondUiTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()
    private lateinit var currentActivity: AssessmentActivity
    private lateinit var uiTestHelper: MotorControlUITestHelper

    private val measureId = "walk-thirty-second"
    private val walk1 = "walking_1"
    private val turnVolumeUp = "turn_up_volume"
    private val pantsWithPocket = "pants_w_pocket"
    private val phoneInPocket = "phone_in_pocket_1"
    private val walk10 = "walking_10"
    private val smoothSurface = "smooth_surface"
    private val pantsWithPocketsIcon = "pants_with_pockets"
    private val walkingShoes = "walking_shoes"

    @Before
    fun navigateThroughInstructions() {
        onView(withText(measureId))
            .perform(click())
        currentActivity = ActivityGetter.getActivityInstance() as AssessmentActivity
        uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId, currentActivity)
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.get_started), imageNames = listOf(
            walk1, smoothSurface, pantsWithPocketsIcon, walkingShoes))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.got_it), imageNames = listOf(walk1))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.volume_up), imageNames = listOf(turnVolumeUp))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.front_pockets), imageNames = listOf(pantsWithPocket))
        uiTestHelper.assertAndClick(uiTestHelper.getString(R.string.put_in_pocket), imageNames = listOf(phoneInPocket))
    }

    @Test
    fun testWalkThirtySeconds() {
        uiTestHelper.performMotionStep(uiTestHelper.getString(R.string.exit), walk10, false)
    }

}