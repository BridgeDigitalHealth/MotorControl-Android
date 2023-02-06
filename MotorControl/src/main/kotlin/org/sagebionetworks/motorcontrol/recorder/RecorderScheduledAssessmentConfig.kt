//
//  RecorderScheduledAssessmentConfig.kt
//
//

package org.sagebionetworks.motorcontrol.recorder

import kotlinx.serialization.Serializable
import org.sagebionetworks.motorcontrol.serialization.BackgroundRecordersConfigurationElement

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