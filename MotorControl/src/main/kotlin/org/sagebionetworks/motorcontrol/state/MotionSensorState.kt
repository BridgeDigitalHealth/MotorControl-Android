//
//  MotionSensorState.kt
//
//

package org.sagebionetworks.motorcontrol.state

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer
import org.sagebionetworks.motorcontrol.recorder.RecorderRunner
import org.sagebionetworks.motorcontrol.utils.MotorControlVibrator

class MotionSensorState(
    override val identifier: String,
    override val hand: HandSelection?,
    override val duration: Double,
    override val context: Context,
    override val spokenInstructions: Map<Int, String>,
    override val restartsOnPause: Boolean,
    override val goForward: () -> Unit,
    override val vibrator: MotorControlVibrator?,
    override val inputResult: MutableSet<Result>?,
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