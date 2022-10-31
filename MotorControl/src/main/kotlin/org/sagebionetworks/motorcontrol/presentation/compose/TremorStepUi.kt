//
//  TremorStepUi.kt
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.R
import org.sagebionetworks.assessmentmodel.presentation.compose.BottomNavigation
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseScreenDialog
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseTopBar
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*

@Composable
internal fun TremorStepUi(
    modifier: Modifier = Modifier,
    assessmentViewModel: AssessmentViewModel?,
    image: Drawable?,
    flippedImage: Boolean,
    imageTintColor: Color?,
    instruction: String?,
    duration: Double,
    countdown: MutableState<Long>) {
    val imageModifier = if (flippedImage) {
        Modifier
            .fillMaxSize()
            .scale(-1F, 1F)
    } else {
        Modifier.fillMaxSize()
    }
    Box {
        Column(modifier = Modifier.background(BackgroundGray)) {
            Box(modifier = Modifier.fillMaxHeight()) {
                if (image != null) {
                    SingleImageUi(
                        image = image,
                        surveyTint = Color(0xFF8FD6FF),
                        imageModifier = imageModifier,
                        imageTintColor = imageTintColor,
                        alpha = 0.5F)
                }
                Column {
                    val openDialog = remember { mutableStateOf(false) }
                    assessmentViewModel?.let {
                        PauseScreenDialog(
                            showDialog = openDialog.value,
                            assessmentViewModel = it,
                        ) {
                            openDialog.value = false
                        }
                    }
                    PauseTopBar(
                        onPauseClicked = { openDialog.value = true },
                        onSkipClicked = { assessmentViewModel?.skip() },
                        showSkip = false
                    )
                    Box(Modifier.padding(vertical = 10.dp)) {
                        StepBodyTextUi(instruction, null, modifier)
                    }
                    CountdownDial(duration = duration, countdown = countdown)
                }
            }
        }
    }
}


@Preview
@Composable
private fun InstructionStepPreview() {
    SageSurveyTheme {
        TremorStepUi(
            assessmentViewModel = null,
            image = null,
            flippedImage = false,
            imageTintColor = null,
            instruction = "",
            duration = 5.0,
            countdown = mutableStateOf(5)
        )
    }
}
