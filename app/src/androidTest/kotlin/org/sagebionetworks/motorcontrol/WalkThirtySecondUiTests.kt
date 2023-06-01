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
    lateinit var currentActivity : AssessmentActivity
    private val measureId = "walk-thirty-second"
    private val uiTestHelper = MotorControlUITestHelper(composeTestRule, measureId = measureId)
    private val exit = "Exit"
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
        uiTestHelper.currentActivity = currentActivity
        uiTestHelper.assertAndClick("Get started", imageNames = listOf(
            walk1, smoothSurface, pantsWithPocketsIcon, walkingShoes))
        uiTestHelper.assertAndClick("Got it", imageNames = listOf(walk1))
        uiTestHelper.assertAndClick("The phone’s volume is up", imageNames = listOf(turnVolumeUp))
        uiTestHelper.assertAndClick("Got front pockets", imageNames = listOf(pantsWithPocket))
        uiTestHelper.assertAndClick("Putting it in my pocket", imageNames = listOf(phoneInPocket))
    }

    @Test
    fun testWalkThirtySeconds() {
        uiTestHelper.performMotionStep(exit, walk10, false)
    }

}