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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.presentation.theme.*

@Composable
internal fun TappingStepUi(
    assessmentViewModel: AssessmentViewModel?,
    image: Drawable?,
    flippedImage: Boolean,
    imageTintColor: Color?,
    timer: StepTimer?,
    duration: Double,
    countdown: MutableState<Long>,
    tapCount: MutableState<Int>) {
    val imageModifier = if (flippedImage) {
        Modifier
            .fillMaxSize()
            .scale(-1F, 1F)
    } else {
        Modifier.fillMaxSize()
    }
    Box(modifier = Modifier
        .fillMaxHeight()
        .background(BackgroundGray)
    ) {
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
                timer = timer
            ) {
                timer?.startTimer(restartsOnPause = false)
            }
            Spacer(modifier = Modifier.weight(1F))
            CountdownDial(
                duration = duration,
                countdown = countdown,
                dialNumber = tapCount,
                dialSubText = stringResource(id = R.string.tap_count)
            )
            Spacer(modifier = Modifier.weight(1F))
            Row {
                Spacer(modifier = Modifier.weight(1F))
                TapButton {
                    tapCount.value += 1
                }
                Spacer(modifier = Modifier.weight(1F))
                TapButton {
                    tapCount.value += 1
                }
                Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}

@Composable
private fun TapButton(
    onTap: () -> Unit,
) {
    val tapButtonSize = 100.dp
    val tapButtonVerticalPad = 48.dp
    Button(
        onClick = onTap,
        modifier = Modifier
            .padding(vertical = tapButtonVerticalPad)
            .size(tapButtonSize),
        shape = CircleShape,
    ) {
        Text(
            text = stringResource(id = R.string.tap_button),
            color = Color.Black,
            style = tapButtonText
        )
    }
}

@Preview
@Composable
private fun InstructionStepPreview() {
    SageSurveyTheme {
        TappingStepUi(
            assessmentViewModel = null,
            image = null,
            flippedImage = false,
            imageTintColor = null,
            timer = null,
            duration = 5.0,
            countdown = mutableStateOf(5),
            tapCount = mutableStateOf(0)
        )
    }
}
