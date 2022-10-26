package org.sagebionetworks.motorcontrol.presentation.compose

import android.os.CountDownTimer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseScreenDialog
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseTopBar
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.R

@Composable
internal fun CountdownStepUi(
    modifier: Modifier = Modifier,
    assessmentViewModel: AssessmentViewModel?,
    duration: Double,
    countdown: MutableState<Long>,
    timer: CountDownTimer?
) {
    Column(
        modifier = modifier
            .background(BackgroundGray)
    ) {
        val openDialog = remember { mutableStateOf(false) }
        assessmentViewModel?.let {
            PauseScreenDialog(
                showDialog = openDialog.value,
                assessmentViewModel = it,
            ) {
                openDialog.value = false
                timer?.start()
            }
        }
        PauseTopBar(
            onPauseClicked = {
                openDialog.value = true
                countdown.value = duration.toLong() * 1000
                timer?.cancel()
            },
            onSkipClicked = { assessmentViewModel?.skip() },
            showSkip = false
        )
        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.begin),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            CountdownDial(duration = duration, countdown = countdown)
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
