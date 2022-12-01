package org.sagebionetworks.motorcontrol.serialization

import kotlinx.serialization.Serializable
import org.sagebionetworks.assessmentmodel.passivedata.recorder.weather.WeatherServiceProviderName

@Serializable
data class BackgroundRecordersConfigurationElement(
    val recorders: List<Recorder>,
    val excludeMapping: Map<String, List<String>>
) {
    @Serializable
    data class Recorder(
        val identifier: String,
        val type: String,
        val services: List<RecorderService>? = listOf()
    )

    @Serializable
    data class RecorderService(
        val identifier: String,
        val type: String,
        val provider: WeatherServiceProviderName,
        val key: String
    )
}

/**
 * Recorder configurations associated with a scheduled assessment's App and Study.
 */
@Serializable
data class RecorderScheduledAssessmentConfig(
    val recorder: BackgroundRecordersConfigurationElement.Recorder,
    val enabledByStudyClientData: Boolean?,
    val disabledByAppForTaskIdentifiers: Set<String>,
    val services: List<BackgroundRecordersConfigurationElement.RecorderService>
) {
    fun isRecorderDisabled(taskId: String): Boolean {
        return disabledByAppForTaskIdentifiers.contains(taskId)
                // present && false disables recorder for this study
                || enabledByStudyClientData?.equals(false) ?: false
    }
}