package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseScreenDialog
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseTopBar

@Composable
fun MotorControlPauseUi(
    assessmentViewModel: AssessmentViewModel?,
    timer: StepTimer? = null,
    onUnpause: () -> Unit = {}
) {
    val openDialog = remember { mutableStateOf(false) }
    assessmentViewModel?.let {
        PauseScreenDialog(
            showDialog = openDialog.value,
            assessmentViewModel = it,
        ) {
            onUnpause()
            openDialog.value = false
        }
    }
    PauseTopBar(
        onPauseClicked = {
            openDialog.value = true
            timer?.stopTimer()
        },
        onSkipClicked = { assessmentViewModel?.skip() },
        showSkip = false
    )
}