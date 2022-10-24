package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil

@Composable
fun CountdownDial(
    duration: Double,
    countdown: MutableState<Long>
) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = (countdown.value / (duration * 1000)).toFloat(),
            color = Color.Black,
            strokeWidth = 7.dp,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .scale(scaleX = -1f, scaleY = 1f)
        )
        Text(
            text = ceil((countdown.value.toDouble() / 1000)).toInt().toString(),
            textAlign = TextAlign.Center,
            fontSize = 75.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}