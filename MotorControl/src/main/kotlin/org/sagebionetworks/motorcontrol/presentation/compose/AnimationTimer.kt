package org.sagebionetworks.motorcontrol.presentation.compose

import android.os.CountDownTimer
import androidx.compose.runtime.MutableState

class AnimationTimer(frames: Int,
                     duration: Double,
                     repeatCount: Int,
                     currentImage: MutableState<Int>) {

    private val intervalLength = ((duration * 1000) / frames).toLong()
    private val timer = object: CountDownTimer(((repeatCount ?: 1000)
            * frames
            * intervalLength), intervalLength) {
        override fun onTick(millisUntilFinished: Long) {
            // Prevents countdown from instantly decrementing
            println("tick")
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