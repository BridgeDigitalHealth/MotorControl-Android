//
//  CountdownDial.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.BackgroundGray
import org.sagebionetworks.motorcontrol.presentation.theme.*
import kotlin.math.ceil

@Composable
fun CountdownDial(
    countdownDuration: Double,
    countdown: MutableState<Long>,
    dialContent: MutableState<Int>? = null,
    dialSubText: String? = null,
    backgroundColor: Color = BackgroundGray
) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = 1 - (countdown.value / (countdownDuration * 1000)).toFloat(),
            color = Color.Black,
            strokeWidth = 7.dp,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .scale(scaleX = 1f, scaleY = 1f)
                .clip(CircleShape)
                .background(backgroundColor)
        )
        Column {
            /*
            CountdownTimer's milliseconds do not always count down to 0 for onFinish().
            This is a way to display 0 at the end of the countdown while keeping ceil(), as
            well as manually set the countdown.value to zero for the progress bar to finish.
             */
            var countdownInt = ceil(countdown.value.toDouble() / 1000).toInt()
            if (countdown.value < 50){
                countdownInt = 0
                countdown.value = 0
            }
            Text(
                text = dialContent?.value?.toString() ?: countdownInt.toString(),
                textAlign = TextAlign.Center,
                style = dialText,
                modifier = Modifier.fillMaxWidth()
            )
            dialSubText?.let {
                Text(
                    text = dialSubText,
                    textAlign = TextAlign.Center,
                    style = dialSecondaryText,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}