package org.sagebionetworks.motorcontrol.presentation.compose

import org.sagebionetworks.assessmentmodel.presentation.compose.CloseTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.R

@Composable
internal fun CountdownStepUi(
    modifier: Modifier = Modifier,
    duration: Double,
    countdown: MutableState<Int>,
    close: ()->Unit,
    hideClose: Boolean = true,
) {
    Column(
        modifier = modifier
            .background(BackgroundGray),
    ) {
        if (!hideClose) {
            CloseTopBar(onCloseClicked = close)
        }
        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(32.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            countdown.let { countdown ->
                Text(
                    text = stringResource(id = R.string.begin),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = (countdown.value / duration).toFloat(),
                        color = Color.Black,
                        strokeWidth = 7.dp,
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                            .scale(scaleX = -1f, scaleY = 1f)
                    )
                    Text(
                        text = (countdown.value).toString(),
                        textAlign = TextAlign.Center,
                        fontSize = 75.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun InstructionStepPreview() {
    SageSurveyTheme {
        CountdownStepUi(duration = 5.0, countdown = mutableStateOf(5), close = {}, hideClose = true)
    }
}
