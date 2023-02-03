//
//  MotorControlVibrator.kt
//
//

package org.sagebionetworks.motorcontrol.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat

class MotorControlVibrator(context: Context) {
        // Vibrator Service is deprecated in API 31. Vibrator Manager for API >= 31
        private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.let {
                ContextCompat.getSystemService(
                    it,
                    VibratorManager::class.java
                )
            } as VibratorManager
            vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrate(milliseconds: Long) {
        val vibrationEffect1: VibrationEffect =
            VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect1)
    }
}