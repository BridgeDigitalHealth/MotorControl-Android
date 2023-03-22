//
//  CountdownDial.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.animation.core.*
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.BackgroundGray
import org.sagebionetworks.motorcontrol.presentation.theme.*

@Composable
fun CountdownDial(
    countdownDuration: Double,
    countdownString: MutableState<String>?,
    paused: MutableState<Boolean>,
    millisLeft: MutableState<Double>,
    countdownFinished: MutableState<Boolean>,
    canBeginCountdown: MutableState<Boolean>,
    timerStartsImmediately: Boolean,
    dialContent: MutableState<Int>? = null,
    dialSubText: String? = null,
    backgroundColor: Color = BackgroundGray
) {
    val countdownDurationMillis = (countdownDuration * 1000).toFloat()
    val animationDurationMillis = (countdownDurationMillis - (countdownDurationMillis - millisLeft.value)).toInt()
    val initialValue = (countdownDurationMillis - (millisLeft.value)) / countdownDurationMillis
    val transition = rememberInfiniteTransition()
    val scale by transition.animateFloat(
        initialValue = if (canBeginCountdown.value && !timerStartsImmediately) initialValue.toFloat() else 0F,
        targetValue = if (canBeginCountdown.value && !paused.value) 1F else 0F,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (timerStartsImmediately) countdownDurationMillis.toInt() else animationDurationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = if (countdownFinished.value) 1F else scale,
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
            Text(
                text = dialContent?.value?.toString() ?: countdownString?.value?.toInt().toString(),
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