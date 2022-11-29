//
//  RecorderRunner.kt
//
//
//  Copyright © 2022 Sage Bionetworks. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package org.sagebionetworks.motorcontrol.recorder

import android.content.Context
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.assessmentmodel.passivedata.asyncaction.AsyncActionConfiguration
import org.sagebionetworks.assessmentmodel.passivedata.recorder.Recorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.audio.AudioRecorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.audio.AudioRecorderConfiguration
import org.sagebionetworks.assessmentmodel.passivedata.recorder.audio.createAudioLevelFlow
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.DeviceMotionJsonFileResultRecorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.MotionRecorderConfiguration
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.createMotionRecorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.sensor.SensorEventComposite
import org.sagebionetworks.assessmentmodel.passivedata.recorder.weather.AndroidWeatherRecorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.weather.WeatherConfiguration
import org.sagebionetworks.assessmentmodel.passivedata.recorder.weather.WeatherServiceConfiguration
import org.sagebionetworks.assessmentmodel.passivedata.recorder.sensor.sensorRecordModule
import org.sagebionetworks.motorcontrol.serialization.BackgroundRecordersConfigurationElement

/**
 * Recorder controller for Mobile Toolbox. Recorders starts with task launch and ends when the task
 * finishes.
 *
 * Does not implement the full feature set of AsyncActions and Recorders defined by AssessmentModel.
 */
class RecorderRunner(
    val context: Context,
    private val httpClient: HttpClient?,
    configs: List<RecorderScheduledAssessmentConfig>,
    private val taskIdentifier: String,
    private val canCreateMotionRecorder: Boolean,
    private val canCreateAudioRecorder: Boolean
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val deferredRecorderResult: Deferred<List<Result>>
    private val recorders: List<Recorder<Result>>

    init {
        this.recorders = configs
            .filterNot { recorderScheduledAssessmentConfig ->
                val isDisabled =
                    recorderScheduledAssessmentConfig.isRecorderDisabled(taskIdentifier)
                if (isDisabled) {
                    Logger.i("Skipping ${recorderScheduledAssessmentConfig.recorder.identifier} disabled for this task")
                }
                isDisabled
            }
            .mapNotNull { recorderFactory(it) }

        deferredRecorderResult =
            scope.async {
                Logger.d("Working in thread ${Thread.currentThread().name}, job ${coroutineContext[Job]}")
                supervisorScope {

                    val results =
                        recorders
                            .mapNotNull { recorder ->
                                Logger.i(
                                    "Awaiting result for recorder: ${recorder.configuration.identifier}"
                                )
                                val deferredResult = recorder.result

                                deferredResult.invokeOnCompletion { throwable ->
                                    when (throwable) {
                                        null -> Logger.d(
                                            "Deferred completed for recorder: ${recorder.configuration.identifier}"
                                        )
                                        is CancellationException -> Logger.d(
                                            "Deferred cancelled for recorder: ${recorder.configuration.identifier}",
                                            throwable
                                        )
                                        else -> Logger.w(
                                            "Deferred threw unhandled exception for recorder: ${recorder.configuration.identifier}",
                                            throwable
                                        )
                                    }
                                }
                                return@mapNotNull try {
                                    val result = deferredResult.await()
                                    Logger.i(
                                        "Finished awaiting result for recorder: ${recorder.configuration.identifier}"
                                    )

                                    result
                                } catch (e: Throwable) {
                                    Logger.w(
                                        "Error waiting for deferred recorder result for recorder: ${recorder.configuration.identifier}",
                                        e
                                    )
                                    null
                                }
                            }


                    Logger.d("Awaited results: $results")
                    return@supervisorScope results
                }
            }
    }

    fun start() {
        Logger.i("Start called")
        scope.coroutineContext.job.start()
        recorders.forEach {
            val recorderId = it.configuration.identifier
            Logger.i("Starting recorder: $recorderId")
            try {
                it.start()
            } catch (e: Exception) {
                Logger.w("Error starting recorder: $recorderId", e)
            }
        }
        Logger.i("Start finished")
    }

    fun stop(): Deferred<List<Result>> {
        Logger.i("Stop called")

        recorders.forEach {
            val recorderId = it.configuration.identifier
            Logger.i("Stopping recorder: $recorderId")
            try {
                it.stop()
            } catch (e: Exception) {
                Logger.w("Error stopping recorder: $recorderId", e)
            }
        }

        return deferredRecorderResult
    }

    fun cancel() {
        recorders.forEach {
            val recorderId = it.configuration.identifier
            Logger.i("Cancelling recorder: $recorderId")
            try {
                it.cancel()
            } catch (e: Exception) {
                Logger.w("Error cancelling recorder: $recorderId", e)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalCoroutinesApi::class)
    internal fun recorderFactory(recorderScheduledAssessmentConfig: RecorderScheduledAssessmentConfig): Recorder<Result>? {
        val recorderConfig = recorderConfigFactory(recorderScheduledAssessmentConfig.recorder)
            ?: return null

        return when (recorderConfig) {

            is WeatherConfiguration -> {
                httpClient?.let {
                    AndroidWeatherRecorder(
                        WeatherConfiguration(
                            recorderConfig.identifier,
                            null,
                            recorderConfig.services
                        ),
                        it,
                        context
                    )
                }
            }
            is MotionRecorderConfiguration -> {
                with(recorderConfig) {
                    DeviceMotionJsonFileResultRecorder(
                        identifier,
                        recorderConfig,
                        CoroutineScope(Dispatchers.IO),
                        createMotionRecorder(context).getSensorData()
                            .mapNotNull {
                                return@mapNotNull if (it is SensorEventComposite.SensorChanged) {
                                    it.sensorEvent
                                } else {
                                    null
                                }
                            },
                        context,
                        Json {
                            serializersModule += sensorRecordModule
                        }
                    )
                }

            }
            is AudioRecorderConfiguration -> {
                with(recorderConfig) {
                    AudioRecorder(
                        identifier = identifier,
                        configuration = recorderConfig,
                        scope = CoroutineScope(Dispatchers.IO),
                        flow = recorderConfig.createAudioLevelFlow(context),
                        context = context
                    )
                }
            }
            else -> {
                Logger.w("Unable to construct recorder ${recorderConfig.identifier}")
                null
            }
        }
    }

    private fun recorderConfigFactory(recorderConfig: BackgroundRecordersConfigurationElement.Recorder): AsyncActionConfiguration? {

        return when (recorderConfig.type) {
            WeatherConfiguration.TYPE -> {
                val services = recorderConfig.services?.map { service ->
                    return@map with(service) {
                        WeatherServiceConfiguration(
                            identifier,
                            provider,
                            key
                        )
                    }
                }

                if (services?.isNotEmpty() == true) {
                    with(recorderConfig) {
                        WeatherConfiguration(
                            identifier,
                            null,
                            services
                        )
                    }
                } else {
                    return null
                }
            }
            MotionRecorderConfiguration.TYPE -> {
                // Check if user has given permission to use motion sensor
                if (canCreateMotionRecorder) {
                    with(recorderConfig) {
                        MotionRecorderConfiguration(
                            identifier = identifier,
                            requiresBackgroundAudio = false,
                            shouldDeletePrevious = false
                        )
                    }
                } else {
                    return null
                }
            }
            AudioRecorderConfiguration.TYPE -> {
                //Check if we have permission to record audio
                if (canCreateAudioRecorder) {
                    with(recorderConfig) {
                        AudioRecorderConfiguration(
                            identifier = identifier
                        )
                    }
                } else {
                    return null
                }
            }
            else -> {
                Logger.w("Unable to construct recorder config ${recorderConfig.identifier}")
                null
            }
        }
    }

    class RecorderRunnerFactory(
        val context: Context,
        private val httpClient: HttpClient?,
        private val canCreateMotionRecorder: Boolean = true,
        private val canCreateAudioRecorder: Boolean = true
    ) {
        private lateinit var configs: List<RecorderScheduledAssessmentConfig>
        fun withConfig(configs: List<RecorderScheduledAssessmentConfig>) {
            this.configs = configs
        }

        fun create(
            taskIdentifier: String
        ): RecorderRunner {
            return RecorderRunner(
                context,
                httpClient,
                configs,
                taskIdentifier,
                canCreateMotionRecorder,
                canCreateAudioRecorder
            )
        }
    }
}

