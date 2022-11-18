//
//  MotorControlTimers.kt
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

import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.MutableState
import kotlin.math.ceil

class StepTimer(
    val countdown: MutableState<Long>,
    val stepDuration: Double,
    val finished: () -> Unit,
    private val textToSpeech: TextToSpeech? = null,
    private val spokenInstructions: Map<Int, String>? = null,
) {
    private var timer: CountDownTimer? = null
    private var instructionsSpoken = mutableSetOf(0)

    fun startTimer(restartsOnPause: Boolean = true) {
        // This is to account for tapping step that does not restart the timer on pause
        val countdownDuration = if (restartsOnPause) {
            stepDuration * 1000
        } else {
            countdown.value
        }
        timer = object: CountDownTimer(countdownDuration.toLong(), 10) {
            override fun onTick(millisUntilFinished: Long) {
                speakAt(stepDuration.toInt() - ceil((millisUntilFinished.toDouble() / 1000)).toInt())
                countdown.value = millisUntilFinished
            }

            override fun onFinish() {
                finished()
                this.cancel()
            }
        }
        timer?.start()
    }

    fun clear() {
        timer?.cancel()
        instructionsSpoken.clear()
    }

    fun stopTimer() {
        timer?.cancel()
    }

    fun speakAt(second: Int) {
        if (!instructionsSpoken.contains(second)) {
            spokenInstructions?.get(second)?.let {
                instructionsSpoken.add(second)
                textToSpeech?.speak(it, TextToSpeech.QUEUE_ADD, null, "")
            }
        }
    }
}

class AnimationTimer(frames: Int,
                     duration: Double,
                     repeatCount: Int?,
                     currentImage: MutableState<Int>) {
    private val intervalLength = ((duration * 1000) / frames).toLong()
    private val timer = object: CountDownTimer(((repeatCount ?: 1000) * frames * intervalLength)
        , intervalLength) {
        override fun onTick(millisUntilFinished: Long) {
            currentImage.value = (currentImage.value + 1) % frames
        }

        override fun onFinish() {
            this.cancel()
        }
    }.start()

    fun stop() {
        timer.cancel()
    }
}