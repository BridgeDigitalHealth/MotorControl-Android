//
//  MotorControlTimers.kt
//
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