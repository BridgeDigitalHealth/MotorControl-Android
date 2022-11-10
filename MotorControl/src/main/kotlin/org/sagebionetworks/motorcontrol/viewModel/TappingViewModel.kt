package org.sagebionetworks.motorcontrol.viewModel

import android.content.Context
import android.os.Handler
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

class TappingViewModel(
    val stepPath: String,
    val hand: HandSelection?,
    val nodeStateResults: TappingResult,
    val duration: Double,
    val context: Context,
    val spokenInstructions: Map<SpokenInstructionTiming, String>,
    val goForward: () -> Unit
) {
    private var startDate: Instant = Clock.System.now()
    private val samples: MutableList<TappingSampleObject> = ArrayList()
    private var previousButton: TappingButtonIdentifier = TappingButtonIdentifier.None
    private var startTime: Long = uptimeMillis()
    val tapCount: MutableState<Int> = mutableStateOf(0)
    val initialTapOccurred: MutableState<Boolean> = mutableStateOf(false)
    val countdown: MutableState<Long> = mutableStateOf(duration.toLong() * 1000)

    private val convertedSpokenInstruction = SpokenInstructionsConverter.convertSpokenInstructions(
        spokenInstructions,
        duration.toInt(),
        hand?.name ?: ""
    )

    lateinit var textToSpeech: TextToSpeech
    init {
        textToSpeech = TextToSpeech(context) {
            textToSpeech.speak(convertedSpokenInstruction[0], TextToSpeech.QUEUE_ADD, null, "")
        }
    }

    val timer = StepTimer(
        countdown = countdown,
        stepDuration = duration,
        finished = {
            setResults()
            val speechListener = object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    // assessmentViewModel.goForward() must be run on main thread
                    Handler(context.mainLooper).post(
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

        if (currentButton == TappingButtonIdentifier.None || previousButton == currentButton) {
            return
        }
        tapCount.value += 1
        previousButton = currentButton
    }

    private fun setResults() {
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