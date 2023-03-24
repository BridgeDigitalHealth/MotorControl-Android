//
//  MotionSensorStepUi.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.presentation.theme.ImageBackgroundColor

@Composable
internal fun MotionSensorStepUi(
    modifier: Modifier = Modifier,
    assessmentViewModel: AssessmentViewModel?,
    title: String,
    countdownString: MutableState<String>,
    countdownFinished: MutableState<Boolean>,
    duration: Double,
    hand: HandSelection?,
    image: Drawable?,
    imageTintColor: Color?,
    cancelCountdown: () -> Unit,
    stopTTS: () -> Unit,
    resetCountdown: () -> Unit,
    startCountdown: () -> Unit,
    paused: MutableState<Boolean>
) {
    val imageModifier = if (hand == HandSelection.RIGHT) {
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
                alpha = 0.5F
            )
        }
        Column {
            MotorControlPauseUi(
                assessmentViewModel = assessmentViewModel,
                stepCompleted = countdownString.value == "0",
                onPause = {
                    cancelCountdown()
                    stopTTS()
                    paused.value = true
                },
                onUnpause = {
                    paused.value = false
                    resetCountdown()
                    startCountdown()
                }
            )
            Box(Modifier.padding(vertical = 10.dp)) {
                StepBodyTextUi(title, null, null, modifier)
            }
            CountdownDialRestart(
                countdownDuration = duration,
                countdownString = countdownString,
                paused = paused,
                millisLeft = remember { mutableStateOf(duration * 1000) },
                countdownFinished = countdownFinished,
                canBeginCountdown = remember { mutableStateOf(true) }
            )
        }
    }
}
