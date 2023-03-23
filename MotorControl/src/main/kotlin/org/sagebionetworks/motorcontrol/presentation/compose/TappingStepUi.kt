//
//  TappingStepUi.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.motorcontrol.presentation.theme.*
import org.sagebionetworks.motorcontrol.serialization.TappingButtonIdentifier

@Composable
internal fun TappingStepUi(
    assessmentViewModel: AssessmentViewModel?,
    countdownTimer: StepTimer,
    countdownDuration: Double,
    initialTapOccurred: MutableState<Boolean>,
    tapCount: MutableState<Int>,
    buttonRectLeft: MutableSet<List<Float>>,
    buttonRectRight: MutableSet<List<Float>>,
    image: Drawable?,
    flippedImage: Boolean,
    imageTintColor: Color?,
    addTappingSample: (TappingButtonIdentifier, List<Float>, Long) -> Unit,
    onFirstTap: () -> Unit,
    stopTimer: () -> Unit,
    startTimer: (Boolean) -> Unit,
    paused: MutableState<Boolean>
) {
    val imageModifier = if (flippedImage) {
        Modifier
            .fillMaxSize()
            .scale(-1F, 1F)
    } else {
        Modifier.fillMaxSize()
    }
    val canBeginCountdown = remember { mutableStateOf(false) }
    Box(modifier = screenModifierWithTapGesture(countdownTimer.countdownFinished, initialTapOccurred, addTappingSample)) {
        if (image != null) {
            SingleImageUi(
                image = image,
                surveyTint = ImageBackgroundColor,
                imageModifier = imageModifier,
                imageTintColor = imageTintColor,
                alpha = 0.5F
            )
        }
        Column {
            MotorControlPauseUi(
                assessmentViewModel = assessmentViewModel,
                stepCompleted = countdownTimer.countdownFinished.value,
                onPause = {
                    stopTimer()
                    paused.value = true
                },
                onUnpause = {
                    paused.value = false
                    startTimer(false)
                }
            )
            Spacer(modifier = Modifier.weight(1F))
            CountdownDial(
                countdownDuration = countdownDuration,
                countdownFinished = countdownTimer.countdownFinished,
                paused = paused,
                millisLeft = countdownTimer.millisLeft,
                canBeginCountdown = canBeginCountdown,
                timerStartsImmediately = false,
                countdownString = null,
                dialContent = tapCount,
                dialSubText = stringResource(id = R.string.tap_count)
            )
            Spacer(modifier = Modifier.weight(1F))
            Row {
                Spacer(modifier = Modifier.weight(1F))
                val firstTap = {
                    onFirstTap()
                    canBeginCountdown.value = true
                }
                TapButton(
                    countdownFinished = countdownTimer.countdownFinished,
                    onFirstTap = firstTap,
                    buttonRect = buttonRectLeft,
                    testTag = "LEFT_BUTTON",
                    onTap = { location, tapDurationInMillis ->
                        addTappingSample(
                            TappingButtonIdentifier.Left,
                            location,
                            tapDurationInMillis
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(1F))
                TapButton(
                    countdownFinished = countdownTimer.countdownFinished,
                    onFirstTap = firstTap,
                    buttonRect = buttonRectRight,
                    testTag = "RIGHT_BUTTON",
                    onTap = { location, tapDurationInMillis ->
                        addTappingSample(
                            TappingButtonIdentifier.Right,
                            location,
                            tapDurationInMillis
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}

@Composable
private fun TapButton(
    countdownFinished: MutableState<Boolean>,
    onFirstTap: () -> Unit = {},
    buttonRect: MutableSet<List<Float>>,
    testTag: String,
    onTap: (location: List<Float>, duration: Long) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = tapButtonModifierWithTapGesture(
            countdownFinished = countdownFinished,
            onFirstTap = onFirstTap,
            buttonRect = buttonRect,
            testTag = testTag,
            onTap = onTap
        )
    ) {
        Text(
            text = stringResource(id = R.string.tap_button),
            color = Color.Black,
            style = tapButtonText,
        )
    }
}

@Composable
fun screenModifierWithTapGesture(
    countdownFinished: MutableState<Boolean>,
    initialTapOccurred: MutableState<Boolean>,
    addTappingSample: (TappingButtonIdentifier, List<Float>, Long) -> Unit
): Modifier {
    return Modifier
        .testTag("TAP_SCREEN")
        .fillMaxHeight()
        .background(BackgroundGray)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { location ->
                    // Ignores tap on screen if countdown is done
                    if (countdownFinished.value) {
                        return@detectTapGestures
                    }
                    lateinit var startOfTapDuration: Instant
                    // The try captures the moment of contact, finally captures moment of release
                    try {
                        startOfTapDuration = Clock.System.now()
                        awaitRelease()
                    } finally {
                        if (initialTapOccurred.value) {
                            addTappingSample(
                                TappingButtonIdentifier.None,
                                listOf(location.x, location.y),
                                Clock.System
                                    .now()
                                    .toEpochMilliseconds()
                                        - startOfTapDuration.toEpochMilliseconds()
                            )
                        }
                    }
                }
            )
        }
}

@Composable
fun tapButtonModifierWithTapGesture(
    countdownFinished: MutableState<Boolean>,
    onFirstTap: () -> Unit = {},
    buttonRect: MutableSet<List<Float>>,
    testTag: String,
    onTap: (location: List<Float>, tapDurationInMillis: Long) -> Unit
): Modifier {
    val xOffset: MutableState<Float> = remember { mutableStateOf(0F) }
    val yOffset: MutableState<Float> = remember { mutableStateOf(0F) }
    val buttonSize = 100F.dp
    return Modifier
        .testTag(testTag)
        .padding(vertical = 48.dp)
        .background(TapButtonColor, shape = CircleShape)
        .size(buttonSize)
        .onGloballyPositioned {
            // This sets the offsets to the top left location of the TapButton within
            // the Root view so that the tap location within the button can be added to the offsets
            // to get the location of the tap with respect to it's location in the whole view
            xOffset.value = it.positionInRoot().x
            yOffset.value = it.positionInRoot().y
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { location ->
                    var startOfTapDuration: Long = 0
                    // Ignores tap on screen if countdown is done
                    if (countdownFinished.value) {
                        return@detectTapGestures
                    }
                    // The try captures the moment of contact, finally captures moment of release
                    try {
                        buttonRect.add(listOf(xOffset.value, yOffset.value))
                        buttonRect.add(listOf(buttonSize.toPx(), buttonSize.toPx()))
                        onFirstTap()
                        startOfTapDuration = Clock.System
                            .now()
                            .toEpochMilliseconds()
                        awaitRelease()
                    } finally {
                        onTap(
                            listOf(
                                location.x + xOffset.value,
                                location.y + yOffset.value
                            ),
                            Clock.System
                                .now()
                                .toEpochMilliseconds()
                                    - startOfTapDuration
                        )
                    }
                }
            )
        }
}
