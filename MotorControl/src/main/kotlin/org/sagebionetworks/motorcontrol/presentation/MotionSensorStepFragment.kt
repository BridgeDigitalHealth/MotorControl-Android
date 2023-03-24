//
//  MotionSensorStepFragment.kt
//
//

package org.sagebionetworks.motorcontrol.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.sagebionetworks.assessmentmodel.presentation.StepFragment
import org.sagebionetworks.assessmentmodel.presentation.databinding.ComposeQuestionStepFragmentBinding
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.SageSurveyTheme
import org.sagebionetworks.assessmentmodel.serialization.loadDrawable
import org.sagebionetworks.motorcontrol.navigation.hand
import org.sagebionetworks.motorcontrol.presentation.compose.MotionSensorStepUi
import org.sagebionetworks.motorcontrol.serialization.MotionSensorStepObject
import org.sagebionetworks.motorcontrol.utils.MotorControlVibrator
import org.sagebionetworks.motorcontrol.utils.SpokenInstructionsConverter
import org.sagebionetworks.motorcontrol.state.MotionSensorState

open class MotionSensorStepFragment: StepFragment() {

    private var _binding: ComposeQuestionStepFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var step: MotionSensorStepObject

    private lateinit var motionSensorState: MotionSensorState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = nodeState.node as MotionSensorStepObject
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = ComposeQuestionStepFragmentBinding.inflate(layoutInflater, container, false)

        val drawable = step.imageInfo?.loadDrawable(requireContext())
        val tint = step.imageInfo?.tint ?: false
        val paused = mutableStateOf(false)

        binding.questionContent.setContent {
            val hand = stepViewModel.nodeState.parent?.node?.hand()

            motionSensorState = MotionSensorState(
                identifier = step.identifier,
                hand = hand,
                duration = step.duration,
                context = requireContext(),
                spokenInstructions = SpokenInstructionsConverter.convertSpokenInstructions(
                    step.spokenInstructions,
                    step.duration.toInt(),
                    hand?.name ?: ""
                ),
                restartsOnPause = true,
                goForward = assessmentViewModel::goForward,
                vibrator = MotorControlVibrator(requireContext()),
                inputResult = stepViewModel.nodeState.parent?.currentResult?.inputResults,
                title = step.title ?: ""
            )
            motionSensorState.start()

            SageSurveyTheme {
                MotionSensorStepUi(
                    assessmentViewModel = assessmentViewModel,
                    title = motionSensorState.title,
                    countdownString = motionSensorState.countdownString,
                    countdownFinished = motionSensorState.timer.countdownFinished,
                    duration = motionSensorState.duration,
                    hand = motionSensorState.hand,
                    image = drawable,
                    imageTintColor = if (tint) {
                        MaterialTheme.colors.primary
                    } else {
                        null
                    },
                    cancelCountdown = motionSensorState::cancel,
                    resetCountdown = { motionSensorState.countdown.value =
                        (motionSensorState.duration * 1000).toLong()},
                    startCountdown = motionSensorState::start,
                    stopTTS = motionSensorState.textToSpeech::stop,
                    paused = paused
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        motionSensorState.cancel()
        super.onDestroyView()
        _binding = null
    }
}