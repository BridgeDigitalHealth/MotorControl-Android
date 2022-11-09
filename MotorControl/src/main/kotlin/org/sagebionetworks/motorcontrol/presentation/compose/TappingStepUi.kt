//
//  TappingStepUi.kt
//
//
//  Copyright © 2022 Sage Bionetworks. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package org.sagebionetworks.motorcontrol.presentation.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.motorcontrol.presentation.theme.*
import org.sagebionetworks.motorcontrol.resultObjects.TappingButtonIdentifier
import org.sagebionetworks.motorcontrol.viewModel.TappingViewModel

@Composable
internal fun TappingStepUi(
    assessmentViewModel: AssessmentViewModel?,
    tappingViewModel: TappingViewModel,
    image: Drawable?,
    flippedImage: Boolean,
    imageTintColor: Color?,
    timer: StepTimer?,
    duration: Double,
    countdown: MutableState<Long>
) {
    val imageModifier = if (flippedImage) {
        Modifier
            .fillMaxSize()
            .scale(-1F, 1F)
    } else {
        Modifier.fillMaxSize()
    }
    Box(modifier = screenModifierWithTapGesture(countdown, tappingViewModel)) {
        if (image != null) {
            SingleImageUi(
                image = image,
                surveyTint = ImageBackgroundColor,
                imageModifier = imageModifier,
                imageTintColor = imageTintColor,
                alpha = 0.5F)
        }
        Column {
            MotorControlPauseUi(
                assessmentViewModel = assessmentViewModel,
                onPause = { timer?.stopTimer() },
                onUnpause = { timer?.startTimer(restartsOnPause = false) }
            )
            Spacer(modifier = Modifier.weight(1F))
            CountdownDial(
                duration = duration,
                countdown = countdown,
                dialContent = tappingViewModel.tapCount,
                dialSubText = stringResource(id = R.string.tap_count)
            )
            Spacer(modifier = Modifier.weight(1F))
            Row {
                Spacer(modifier = Modifier.weight(1F))
                TapButton(
                    timer,
                    tappingViewModel.initialTapOccurred,
                    countdown
                ) { location, duration ->
                    tappingViewModel.addTappingSample(
                        currentButton = TappingButtonIdentifier.Left,
                        location = location,
                        duration = duration
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                TapButton(
                    timer,
                    tappingViewModel.initialTapOccurred,
                    countdown
                ) { location, duration ->
                    tappingViewModel.addTappingSample(
                        currentButton = TappingButtonIdentifier.Right,
                        location = location,
                        duration = duration
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}

@Composable
private fun TapButton(
    timer: StepTimer?,
    initialTapOccurred: MutableState<Boolean>,
    countdown: MutableState<Long>,
    onTap: (location: List<Float>, duration: Long) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = tapButtonModifierWithTapGesture(
            initialTapOccurred,
            countdown,
            timer,
            onTap
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
    countdown: MutableState<Long>,
    tappingViewModel: TappingViewModel
): Modifier {
    return Modifier
        .fillMaxHeight()
        .background(BackgroundGray)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { location ->
                    if (countdown.value <= 0) {
                        return@detectTapGestures
                    }
                    lateinit var startDuration: Instant
                    try {
                        startDuration = Clock.System.now()
                        awaitRelease()
                    } finally {
                        if (tappingViewModel.initialTapOccurred.value) {
                            tappingViewModel.addTappingSample(
                                currentButton = TappingButtonIdentifier.None,
                                location = listOf(location.x, location.y),
                                duration = Clock.System.now().toEpochMilliseconds()
                                        - startDuration.toEpochMilliseconds()
                            )
                        }
                    }
                }
            )
        }
}

@Composable
fun tapButtonModifierWithTapGesture(
    initialTapOccurred: MutableState<Boolean>,
    countdown: MutableState<Long>,
    timer: StepTimer?,
    onTap: (location: List<Float>, duration: Long) -> Unit
): Modifier {
    val xOffset: MutableState<Float> = remember { mutableStateOf(0F) }
    val yOffset: MutableState<Float> = remember { mutableStateOf(0F) }
    return Modifier
        .padding(vertical = 48.dp)
        .background(TapButtonColor, shape = CircleShape)
        .size(100.dp)
        .onGloballyPositioned {
            xOffset.value = it.positionInRoot().x
            yOffset.value = it.positionInRoot().y
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { location ->
                    lateinit var startDuration: Instant
                    if (countdown.value <= 0) {
                        return@detectTapGestures
                    }
                    try {
                        if (!initialTapOccurred.value) {
                            timer?.startTimer()
                            initialTapOccurred.value = true
                        }
                        startDuration = Clock.System.now()
                        awaitRelease()
                    } finally {
                        onTap(
                            listOf(location.x + xOffset.value, location.y + yOffset.value),
                            Clock.System.now().toEpochMilliseconds()
                                    - startDuration.toEpochMilliseconds()
                        )
                    }
                }
            )
        }
}

@Preview
@Composable
private fun InstructionStepPreview() {
    SageSurveyTheme {
        TappingStepUi(
            assessmentViewModel = null,
            tappingViewModel = TappingViewModel("", null),
            image = null,
            flippedImage = false,
            imageTintColor = null,
            timer = null,
            duration = 5.0,
            countdown = mutableStateOf(5)
        )
    }
}
