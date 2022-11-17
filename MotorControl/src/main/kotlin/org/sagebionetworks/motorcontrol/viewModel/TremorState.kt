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
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.sagebionetworks.assessmentmodel.SpokenInstructionTiming
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.MotionRecorderConfiguration
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.MotionRecorderType
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer
import org.sagebionetworks.motorcontrol.recorder.MotionRecorderRunner
import org.sagebionetworks.motorcontrol.utils.MotorControlVibrator
import org.sagebionetworks.motorcontrol.utils.SpokenInstructionsConverter

class TremorState(
    override val hand: HandSelection?,
    override val duration: Double,
    override val context: Context,
    override val spokenInstructions: Map<SpokenInstructionTiming, String>,
    override val goForward: () -> Unit,
    val identifier: String,
    var title: String
) : ActiveStep {
    override val countdown: MutableState<Long> = mutableStateOf(duration.toLong() * 1000)
    override lateinit var textToSpeech: TextToSpeech
    private val vibrator = MotorControlVibrator(context)
    private val recorderRunner: MotionRecorderRunner = MotionRecorderRunner(
        context,
        MotionRecorderConfiguration(
            identifier = "${identifier}/${hand?.name}",
            startStepIdentifier = identifier,
            stopStepIdentifier = identifier,
            requiresBackgroundAudio = true,
            recorderTypes = MotionRecorderType.all,
            frequency = duration
        )
    )
    private val convertedSpokenInstruction = SpokenInstructionsConverter.convertSpokenInstructions(
        spokenInstructions,
        duration.toInt(),
        hand?.name ?: ""
    )

    init {
        title = title.replace("%@", hand?.name ?: "")
        textToSpeech = TextToSpeech(context) {
            textToSpeech.speak(convertedSpokenInstruction[0], android.speech.tts.TextToSpeech.QUEUE_ADD, null, "")
        }
        vibrator.vibrate(500)
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

    override fun start() {
        recorderRunner.start()
        timer.startTimer()
    }

    override fun finished() {
        CoroutineScope(Dispatchers.IO).launch {
            println(recorderRunner.stop().await())
        }
    }

}