package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.compose.BlackButton
import org.sagebionetworks.assessmentmodel.presentation.compose.BlackNextButton
import org.sagebionetworks.assessmentmodel.presentation.compose.WhiteBackButton

@Composable
fun MotorControlBottomNavigation(
    onBackClicked: () -> Unit = {},
    onNextClicked: () -> Unit,
    nextText: String? = null,
    backEnabled: Boolean = true,
    nextEnabled: Boolean = true
) {
    Row(modifier = Modifier
        .padding(top = 10.dp, bottom = 10.dp)
        .padding(horizontal = 20.dp)
        .fillMaxWidth()) {
        if(backEnabled) {
            WhiteBackButton(onClick = onBackClicked)
        }
        Spacer(modifier = Modifier.weight(1f))
        if (nextText != null) {
            BlackButton(onClick = onNextClicked, enabled = nextEnabled, text = nextText)
        } else {
            BlackNextButton(onClick = onNextClicked, enabled = nextEnabled)
        }
    }
}