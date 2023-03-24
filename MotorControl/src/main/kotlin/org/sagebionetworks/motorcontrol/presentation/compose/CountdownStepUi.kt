//
//  CountdownStepUi.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.motorcontrol.presentation.theme.countdownBeginText
import org.sagebionetworks.motorcontrol.utils.StepTimer

@Composable
internal fun CountdownStepUi(
    assessmentViewModel: AssessmentViewModel?,
    duration: Double,
    countdown: MutableState<Long>,
    timer: StepTimer,
    paused: MutableState<Boolean>
) {
    Column(modifier = Modifier.background(BackgroundGray)) {
        MotorControlPauseUi(
            assessmentViewModel = assessmentViewModel,
            onPause = {
                timer.clear()
                paused.value = true
            },
            onUnpause = {
                // Resets countdown to initial value
                paused.value = false
                countdown.value = (duration * 1000).toLong()
                timer.startTimer()
            }
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.begin),
                textAlign = TextAlign.Center,
                style = countdownBeginText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            CountdownDialRestart(
                countdownDuration = duration,
                canBeginCountdown = remember { mutableStateOf(true) },
                paused = paused,
                countdownFinished = timer.countdownFinished,
                countdownString = timer.countdownString,
                millisLeft = timer.millisLeft,
                dialSubText = null
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
