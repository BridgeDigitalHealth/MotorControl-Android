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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.motorcontrol.presentation.theme.countdownBeginText

@Composable
internal fun CountdownStepUi(
    assessmentViewModel: AssessmentViewModel?,
    duration: Double,
    countdown: MutableState<Long>,
    timer: StepTimer?
) {
    Column(modifier = Modifier.background(BackgroundGray)) {
        MotorControlPauseUi(
            assessmentViewModel = assessmentViewModel,
            onPause = { timer?.clear() },
            onUnpause = {
                countdown.value = (duration * 1000).toLong() // Resets countdown to initial value
                timer?.startTimer()
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
            CountdownDial(countdownDuration = duration, countdown = countdown)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun InstructionStepPreview() {
    SageSurveyTheme {
        CountdownStepUi(
            assessmentViewModel = null,
            duration = 5.0,
            countdown = mutableStateOf(5),
            timer = null
        )
    }
}
