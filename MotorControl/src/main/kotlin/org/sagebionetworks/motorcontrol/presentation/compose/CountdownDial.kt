//
//  CountdownDial.kt
//
//
//  Copyright © 2022 Sage Bionetworks. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    duration: Double,
    countdown: MutableState<Long>,
    dialContent: MutableState<Int>? = null,
    dialSubText: String? = null,
    backgroundColor: Color = BackgroundGray
) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = 1 - (countdown.value / (duration * 1000)).toFloat(),
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
            This is a way to display 0 at the end of the countdown while keeping ceil().
             */
            val countdownInt = if (countdown.value < 50){
                0
            } else {
                ceil(countdown.value.toDouble() / 1000).toInt()
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