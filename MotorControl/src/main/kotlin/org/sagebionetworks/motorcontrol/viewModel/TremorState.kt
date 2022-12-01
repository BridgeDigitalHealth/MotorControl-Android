//
//  TremorState.kt
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

package org.sagebionetworks.motorcontrol.viewModel

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer
import org.sagebionetworks.motorcontrol.recorder.RecorderRunner
import org.sagebionetworks.motorcontrol.utils.MotorControlVibrator

class TremorState(
    override val identifier: String,
    override val hand: HandSelection?,
    override val duration: Double,
    override val context: Context,
    override val spokenInstructions: Map<Int, String>,
    override val restartsOnPause: Boolean,
    override val goForward: () -> Unit,
    override val vibrator: MotorControlVibrator?,
    var title: String
) : ActiveStep {
    override val countdown: MutableState<Long> = mutableStateOf(duration.toLong() * 1000)
    override lateinit var textToSpeech: TextToSpeech
    override lateinit var recorderRunnerFactory: RecorderRunner.RecorderRunnerFactory
    override lateinit var recorderRunner: RecorderRunner

    init {
        createMotionSensor()
        title = title.replace("%@", hand?.name ?: "")
        textToSpeech = TextToSpeech(context) {
            textToSpeech.speak(spokenInstructions[0], TextToSpeech.QUEUE_ADD, null, "")
        }
    }

    override val timer = StepTimer(
        countdown = countdown,
        stepDuration = duration,
        finished = {
            stopRecorder()
            speakAtCompleted()
        },
        textToSpeech = textToSpeech,
        spokenInstructions = spokenInstructions
    )
}