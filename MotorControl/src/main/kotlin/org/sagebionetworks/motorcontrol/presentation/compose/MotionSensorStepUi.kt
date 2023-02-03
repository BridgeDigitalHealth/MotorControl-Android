//
//  MotionSensorStepUi.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
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
import org.sagebionetworks.motorcontrol.state.MotionSensorState

@Composable
internal fun MotionSensorStepUi(
    modifier: Modifier = Modifier,
    assessmentViewModel: AssessmentViewModel?,
    motionSensorState: MotionSensorState,
    image: Drawable?,
    imageTintColor: Color?
) {
    val imageModifier = if (motionSensorState.hand == HandSelection.RIGHT) {
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
                stepCompleted = motionSensorState.countdown.value == 0L,
                onPause = {
                    motionSensorState.cancel()
                    motionSensorState.textToSpeech.stop()
                },
                onUnpause = {
                    motionSensorState.countdown.value = (motionSensorState.duration * 1000).toLong() // Resets countdown to initial value
                    motionSensorState.start()
                }
            )
            Box(Modifier.padding(vertical = 10.dp)) {
                StepBodyTextUi(motionSensorState.title, null, null, modifier)
            }
            CountdownDial(
                countdownDuration = motionSensorState.duration,
                countdown = motionSensorState.countdown,
                dialSubText = stringResource(id = R.string.seconds)
            )
        }
    }
}
