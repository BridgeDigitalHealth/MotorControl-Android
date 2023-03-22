//
//  TappingState.kt
//
//

package org.sagebionetworks.motorcontrol.state

import android.content.Context
import android.os.SystemClock.uptimeMillis
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer
import org.sagebionetworks.motorcontrol.recorder.RecorderRunner
import org.sagebionetworks.motorcontrol.serialization.TappingButtonIdentifier
import org.sagebionetworks.motorcontrol.serialization.TappingResultObject
import org.sagebionetworks.motorcontrol.serialization.TappingSampleObject
import org.sagebionetworks.motorcontrol.utils.MotorControlVibrator

class TappingState(
    override val identifier: String,
    override val hand: HandSelection?,
    override val duration: Double,
    override val context: Context,
    override val spokenInstructions: MutableMap<Int, String>,
    override val restartsOnPause: Boolean,
    override val goForward: () -> Unit,
    override val vibrator: MotorControlVibrator?,
    override val inputResult: MutableSet<Result>?,
    val nodeStateResults: TappingResultObject,
    val stepPath: String,
    val buttonRectLeft: MutableSet<List<Float>> = mutableSetOf(),
    val buttonRectRight: MutableSet<List<Float>> = mutableSetOf()
) : ActiveStep{
    override val countdown: MutableState<Long> = mutableStateOf(duration.toLong() * 1000)
    override lateinit var textToSpeech: TextToSpeech
    override lateinit var recorderRunnerFactory: RecorderRunner.RecorderRunnerFactory
    override lateinit var recorderRunner: RecorderRunner
    private var startDate: Instant = Clock.System.now()
    private val samples: MutableList<TappingSampleObject> = ArrayList()
    private var previousButton: TappingButtonIdentifier = TappingButtonIdentifier.None
    private var startTime: Long = uptimeMillis()
    val tapCount: MutableState<Int> = mutableStateOf(0)
    val initialTapOccurred: MutableState<Boolean> = mutableStateOf(false)
    private val millisLeft = mutableStateOf(duration * 1000)

    init {
        createMotionSensor()
        textToSpeech = TextToSpeech(context) {
            textToSpeech.speak(spokenInstructions[0], TextToSpeech.QUEUE_ADD, null, "")
        }
    }

    override val timer = StepTimer(
        countdown = countdown,
        millisLeft = millisLeft,
        countdownString = mutableStateOf(""),
        stepDuration = duration,
        finished = {
            stopRecorder()
            speakAtCompleted()
        },
        textToSpeech = textToSpeech,
        spokenInstructions = spokenInstructions
    )

    override fun stopRecorder() {
        super.stopRecorder()
        nodeStateResults.startDateTime = startDate
        nodeStateResults.endDateTime = Clock.System.now()
        nodeStateResults.hand = hand?.name?.lowercase() ?: ""
        nodeStateResults.buttonRectLeft = buttonRectLeft.toString()
        nodeStateResults.buttonRectRight = buttonRectRight.toString()
        nodeStateResults.samples = samples
        nodeStateResults.tapCount = tapCount.value
    }

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

    fun onFirstTap() {
        if (!initialTapOccurred.value) {
            start()
            initialTapOccurred.value = true
            startTime = uptimeMillis()
        }
    }
}