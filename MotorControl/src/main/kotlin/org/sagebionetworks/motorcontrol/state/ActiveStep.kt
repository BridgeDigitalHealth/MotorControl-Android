//
//  ActiveStep.kt
//
//

package org.sagebionetworks.motorcontrol.state

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.MutableState
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.MotionRecorderConfiguration
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer
import org.sagebionetworks.motorcontrol.recorder.RecorderRunner
import org.sagebionetworks.motorcontrol.recorder.RecorderScheduledAssessmentConfig
import org.sagebionetworks.motorcontrol.serialization.BackgroundRecordersConfigurationElement
import org.sagebionetworks.motorcontrol.utils.MotorControlVibrator

interface ActiveStep {
    val identifier: String
    val hand: HandSelection?
    val duration: Double
    val context: Context
    val spokenInstructions: Map<Int, String>
    val goForward: () -> Unit
    val countdown: MutableState<Long>
    var textToSpeech: TextToSpeech
    val restartsOnPause: Boolean
    val timer: StepTimer
    var recorderRunnerFactory: RecorderRunner.RecorderRunnerFactory
    var recorderRunner: RecorderRunner
    val vibrator: MotorControlVibrator?
    val inputResult: MutableSet<Result>?

    fun createMotionSensor() {
        recorderRunnerFactory = RecorderRunner.RecorderRunnerFactory(context, null)
        recorderRunnerFactory.withConfig(
            listOf(
                RecorderScheduledAssessmentConfig(
                    recorder = BackgroundRecordersConfigurationElement.Recorder(
                        "${hand?.name?.lowercase()}_$identifier",
                        MotionRecorderConfiguration.TYPE
                    ),
                    disabledByAppForTaskIdentifiers = setOf(),
                    enabledByStudyClientData = true,
                    services = listOf()
                )
            )
        )
        recorderRunner = recorderRunnerFactory.create(identifier)
    }

    fun start() {
        vibrator?.vibrate(500)
        recorderRunner.start()
        timer.startTimer(restartsOnPause = restartsOnPause)
    }

    fun cancel() {
        timer.clear()
        try {
            recorderRunner.cancel()
        } catch (e: Exception) {
            Logger.w("Error cancelling recorder", e)
        }
    }

    fun stopRecorder() {
        vibrator?.vibrate(500)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (result in recorderRunner.stop().await()) {
                    inputResult?.add(result)
                }
            } catch (e: Exception) {
                Logger.w("Error stopping recorder", e)
            }
        }
    }

    fun speakAtCompleted() {
        val speechListener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                // assessmentViewModel.goForward() must be run on main thread
                Handler(Looper.getMainLooper()).post(
                    kotlinx.coroutines.Runnable {
                        textToSpeech.shutdown()
                        goForward()
                    }
                )
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {}
        }
        textToSpeech.setOnUtteranceProgressListener(speechListener)
        textToSpeech.speak(
            spokenInstructions[duration.toInt()],
            TextToSpeech.QUEUE_ADD,
            null,
            ""
        )
    }
}