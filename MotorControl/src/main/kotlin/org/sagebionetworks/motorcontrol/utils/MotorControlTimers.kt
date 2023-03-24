//
//  MotorControlTimers.kt
//
//

package org.sagebionetworks.motorcontrol.utils

import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.math.ceil

class StepTimer(
    val countdown: MutableState<Long>,
    val millisLeft: MutableState<Double>,
    val countdownString: MutableState<String>,
    val stepDuration: Double,
    val finished: () -> Unit,
    private val textToSpeech: TextToSpeech? = null,
    private val spokenInstructions: Map<Int, String>? = null,
) {
    val countdownFinished = mutableStateOf(false)
    private var timer: CountDownTimer? = null
    private var instructionsSpoken = mutableSetOf(0)

    fun startTimer(restartsOnPause: Boolean = true) {
        // This accounts for steps that do not restart the timer on pause
        if (!restartsOnPause) {
            millisLeft.value = countdown.value.toDouble()
        }
        countdownString.value = (millisLeft.value / 1000).toInt().toString()
        timer = object: CountDownTimer(millisLeft.value.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val second = ceil((millisUntilFinished.toDouble() / 1000)).toInt()
                if(second.toString() != countdownString.value) {
                    countdownString.value = second.toString()
                }
                speakAt(stepDuration.toInt() - second)
                countdown.value = millisUntilFinished
            }

            override fun onFinish() {
                countdownString.value = "0"
                countdownFinished.value = true
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