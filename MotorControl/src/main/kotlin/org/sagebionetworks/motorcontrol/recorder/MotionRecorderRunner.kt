package org.sagebionetworks.motorcontrol.recorder

import android.content.Context
import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import org.sagebionetworks.assessmentmodel.passivedata.ResultData
import org.sagebionetworks.assessmentmodel.passivedata.recorder.Recorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.DeviceMotionJsonFileResultRecorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.MotionRecorderConfiguration
import org.sagebionetworks.assessmentmodel.passivedata.recorder.motion.createMotionRecorder
import org.sagebionetworks.assessmentmodel.passivedata.recorder.sensor.SensorEventComposite
import org.sagebionetworks.assessmentmodel.passivedata.recorder.sensor.sensorRecordModule

@OptIn(ExperimentalSerializationApi::class, ExperimentalCoroutinesApi::class)
class MotionRecorderRunner(
    val context: Context,
    config: MotionRecorderConfiguration,
    ) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val deferredRecorderResult: Deferred<ResultData?>
    private val recorder: Recorder<ResultData>

    init {
        with(config) {
            recorder = DeviceMotionJsonFileResultRecorder(
                identifier,
                config,
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

        deferredRecorderResult =
            scope.async {
                Logger.d("Working in thread ${Thread.currentThread().name}, job ${coroutineContext[Job]}")
                supervisorScope {

                    Logger.i(
                        "Awaiting result for recorder: ${recorder.configuration.identifier}"
                    )
                    val deferredResult = recorder.result

                    deferredResult.invokeOnCompletion { throwable ->
                        when (throwable) {
                            null -> {
                                Logger.d(
                                    "Deferred completed for recorder: ${config.identifier}"
                                )
                            }
                            is CancellationException -> {
                                Logger.d(
                                    "Deferred cancelled for recorder: ${config.identifier}",
                                    throwable
                                )
                            }
                            else -> {
                                Logger.w(
                                    "Deferred threw unhandled exception for recorder: ${config.identifier}",
                                    throwable
                                )
                            }
                        }
                    }

                    Logger.d("Awaited result: $deferredResult")
                    return@supervisorScope deferredResult.await()
                }
            }
    }

    fun start() {
        Logger.i("Start called")
        scope.coroutineContext.job.start()
        val recorderId = recorder.configuration.identifier
        Logger.i("Starting recorder: $recorderId")
        try {
            recorder.start()
        } catch (e: Exception) {
            Logger.w("Error starting recorder: $recorderId", e)
        }
        Logger.i("Start finished")
    }

    suspend fun stop(): ResultData? {
        Logger.i("Stop called")

        val recorderId = recorder.configuration.identifier
        Logger.i("Stopping recorder: $recorderId")
        try {
            recorder.stop()
        } catch (e: Exception) {
            Logger.w("Error stopping recorder: $recorderId", e)
        }
        return deferredRecorderResult.await()
    }

    fun cancel() {
        val recorderId = recorder.configuration.identifier
        Logger.i("Cancelling recorder: $recorderId")
        try {
            recorder.cancel()
        } catch (e: Exception) {
            Logger.w("Error cancelling recorder: $recorderId", e)
        }
    }
}


