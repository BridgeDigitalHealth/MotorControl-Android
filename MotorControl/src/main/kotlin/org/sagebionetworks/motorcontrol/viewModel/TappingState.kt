//
//  TappingState.kt
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
import android.os.Handler
import android.os.Looper
import android.os.SystemClock.uptimeMillis
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.sagebionetworks.assessmentmodel.SpokenInstructionTiming
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer
import org.sagebionetworks.motorcontrol.resultObjects.TappingButtonIdentifier
import org.sagebionetworks.motorcontrol.resultObjects.TappingResult
import org.sagebionetworks.motorcontrol.serialization.TappingSampleObject
import org.sagebionetworks.motorcontrol.utils.SpokenInstructionsConverter

class TappingState(
    override val hand: HandSelection?,
    override val duration: Double,
    override val context: Context,
    override val spokenInstructions: Map<SpokenInstructionTiming, String>,
    override val goForward: () -> Unit,
    val nodeStateResults: TappingResult,
    val stepPath: String,
) : ActiveStep{
    override val countdown: MutableState<Long> = mutableStateOf(duration.toLong() * 1000)
    private var startDate: Instant = Clock.System.now()
    private val samples: MutableList<TappingSampleObject> = ArrayList()
    private var previousButton: TappingButtonIdentifier = TappingButtonIdentifier.None
    private var startTime: Long = uptimeMillis()
    val tapCount: MutableState<Int> = mutableStateOf(0)
    val initialTapOccurred: MutableState<Boolean> = mutableStateOf(false)

    private val convertedSpokenInstruction = SpokenInstructionsConverter.convertSpokenInstructions(
        spokenInstructions,
        duration.toInt(),
        hand?.name ?: ""
    )

    override lateinit var textToSpeech: TextToSpeech
    init {
        textToSpeech = TextToSpeech(context) {
            textToSpeech.speak(convertedSpokenInstruction[0], TextToSpeech.QUEUE_ADD, null, "")
        }
    }

    override val timer = StepTimer(
        countdown = countdown,
        stepDuration = duration,
        finished = {
            finished()
            val speechListener = object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    // assessmentViewModel.goForward() must be run on main thread
                    Handler(Looper.getMainLooper()).post(
                        kotlinx.coroutines.Runnable {
                            goForward()
                        }
                    )
                }
                override fun onError(utteranceId: String?) {}
            }
            textToSpeech.setOnUtteranceProgressListener(speechListener)
            textToSpeech.speak(
                convertedSpokenInstruction[duration.toInt()],
                TextToSpeech.QUEUE_ADD,
                null,
                ""
            )
        },
        textToSpeech = textToSpeech,
        spokenInstructions = convertedSpokenInstruction
    )

    fun addTappingSample(currentButton: TappingButtonIdentifier,
                         location: List<Float>,
                         tapDurationInMillis: Long
    ) {
        val sample = TappingSampleObject(
            uptime = uptimeMillis().toDouble() / 1000.0,
            timestamp = (uptimeMillis() - startTime - tapDurationInMillis).toDouble() / 1000.0,
            stepPath = stepPath,
            buttonIdentifier = currentButton.name.lowercase(),
            location = location,
            duration = tapDurationInMillis.toDouble() / 1000.0
        )
        samples.add(sample)

        // Increment tapCount if tap was inside button and current button tapped is not the same
        // as the last button tapped
        if (currentButton == TappingButtonIdentifier.None || previousButton == currentButton) {
            return
        }
        tapCount.value += 1
        previousButton = currentButton
    }

    override fun start() {
        timer.startTimer()
    }

    override fun finished() {
        nodeStateResults.startDateTime = startDate
        nodeStateResults.endDateTime = Clock.System.now()
        nodeStateResults.hand = hand?.name ?: ""
        nodeStateResults.samples = samples
        nodeStateResults.tapCount = tapCount.value
    }

    fun onFirstTap() {
        if (!initialTapOccurred.value) {
            timer.startTimer()
            initialTapOccurred.value = true
            startTime = uptimeMillis()
        }
    }
}