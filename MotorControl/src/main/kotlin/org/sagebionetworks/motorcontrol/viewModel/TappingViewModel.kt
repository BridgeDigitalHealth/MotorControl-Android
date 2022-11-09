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
    private var startDate: Instant = Clock.System.now()
    private val samples: MutableList<TappingSampleObject> = ArrayList()
    private var previousButton: TappingButtonIdentifier = TappingButtonIdentifier.None
    val tapCount: MutableState<Int> = mutableStateOf(0)
    val initialTapOccurred: MutableState<Boolean> = mutableStateOf(false)
    var startTime: Long = uptimeMillis()

    fun addTappingSample(currentButton: TappingButtonIdentifier,
                         location: List<Float>,
                         tapDurationInMillis: Long
    ) {
        val sample = TappingSampleObject(
            uptime = uptimeMillis().toFloat() / 1000,
            timestamp = (uptimeMillis() - startTime - tapDurationInMillis).toFloat() / 1000,
            stepPath = stepPath,
            buttonIdentifier = currentButton.name.lowercase(),
            location = location,
            duration = tapDurationInMillis.toFloat() / 1000
        )
        samples.add(sample)

        if (currentButton == TappingButtonIdentifier.None || previousButton == currentButton) {
            return
        }
        tapCount.value += 1
        previousButton = currentButton
    }

    fun getResult(): TappingResultObject {
        return TappingResultObject(
            identifier = "tapping",
            startDateTime = startDate,
            endDateTime = Clock.System.now(),
            hand = handSelection?.name?.lowercase() ?: "",
            samples = samples,
            tapCount = tapCount.value
        )
    }

    fun setStartTime() {
        startTime = uptimeMillis()
    }
}