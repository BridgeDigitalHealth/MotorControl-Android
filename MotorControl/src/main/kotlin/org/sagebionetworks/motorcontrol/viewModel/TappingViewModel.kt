package org.sagebionetworks.motorcontrol.viewModel

import android.os.SystemClock.uptimeMillis
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.resultObjects.TappingButtonIdentifier
import org.sagebionetworks.motorcontrol.serialization.TappingResultObject
import org.sagebionetworks.motorcontrol.serialization.TappingSampleObject

class TappingViewModel(val stepPath: String, val handSelection: HandSelection?) {
    private val startDate: Instant = Clock.System.now()
    private val startTime: Long = uptimeMillis()
    private val samples: MutableList<TappingSampleObject> = ArrayList()
    val tapCount: MutableState<Int> = mutableStateOf(0)
    val initialTapOccurred: MutableState<Boolean> = mutableStateOf(false)
    var previousButton: TappingButtonIdentifier = TappingButtonIdentifier.None

    fun addTappingSample(currentButton: TappingButtonIdentifier,
                         location: List<Float>,
                         duration: Long
    ) {
        val sample = TappingSampleObject(
            uptime = uptimeMillis(),
            timestamp = uptimeMillis() - startTime - duration,
            stepPath = stepPath,
            buttonIdentifier = currentButton,
            location = location,
            duration = duration.toFloat() / 1000
        )
        println(sample)
        samples.add(sample)

        if (currentButton == TappingButtonIdentifier.None || previousButton == currentButton) {
            return
        }
        tapCount.value += 1
        previousButton = currentButton
    }

    fun exportJSON() {
        val tappingResultObject = TappingResultObject(
            identifier = "tapping",
            startDate = startDate,
            endDate = Clock.System.now(),
            hand = handSelection?.name ?: "none",
            sample = samples,
            tapCount = tapCount.value
        )
    }
}