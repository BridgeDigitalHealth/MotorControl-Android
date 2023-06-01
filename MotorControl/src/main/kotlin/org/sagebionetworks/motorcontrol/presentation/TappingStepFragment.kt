//
//  TappingStepFragment.kt
//
//

package org.sagebionetworks.motorcontrol.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import org.sagebionetworks.assessmentmodel.presentation.StepFragment
import org.sagebionetworks.assessmentmodel.presentation.databinding.ComposeQuestionStepFragmentBinding
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.SageSurveyTheme
import org.sagebionetworks.assessmentmodel.serialization.loadDrawable
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.navigation.hand
import org.sagebionetworks.motorcontrol.presentation.compose.TappingStepUi
import org.sagebionetworks.motorcontrol.serialization.TappingResultObject
import org.sagebionetworks.motorcontrol.serialization.TappingStepObject
import org.sagebionetworks.motorcontrol.utils.SpokenInstructionsConverter
import org.sagebionetworks.motorcontrol.state.TappingState

open class TappingStepFragment: StepFragment() {

    private var _binding: ComposeQuestionStepFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var step: TappingStepObject

    private lateinit var tappingState: TappingState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = nodeState.node as TappingStepObject
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = ComposeQuestionStepFragmentBinding.inflate(layoutInflater, container, false)
        val drawable = step.imageInfo?.loadDrawable(requireContext())
        val tint = step.imageInfo?.tint ?: false
        val paused = mutableStateOf(false)
        binding.questionContent.setContent {
            val hand = stepViewModel.nodeState.parent?.node?.hand()
            tappingState = TappingState(
                identifier = step.identifier,
                hand = hand,
                duration = step.duration,
                context = requireContext(),
                spokenInstructions = SpokenInstructionsConverter.convertSpokenInstructions(
                    step.spokenInstructions,
                    step.duration.toInt(),
                    hand?.name ?: ""
                ),
                restartsOnPause = false,
                goForward = assessmentViewModel::goForward,
                vibrator = null,
                inputResult = stepViewModel.nodeState.parent?.currentResult?.inputResults,
                nodeStateResults = stepViewModel.nodeState.currentResult as TappingResultObject,
                stepPath = "Tapping/${hand?.name?.lowercase()}"
            )

            SageSurveyTheme {
                TappingStepUi(
                    assessmentViewModel = assessmentViewModel,
                    countdownTimer = tappingState.timer,
                    countdownDuration = tappingState.duration,
                    initialTapOccurred = tappingState.initialTapOccurred,
                    tapCount = tappingState.tapCount,
                    buttonRectLeft = tappingState.buttonRectLeft,
                    buttonRectRight = tappingState.buttonRectRight,
                    image = drawable,
                    imageName = step.imageInfo?.imageName ?: "IMAGE",
                    flippedImage = stepViewModel.nodeState.parent?.node?.hand()
                            == HandSelection.RIGHT,
                    imageTintColor = if (tint) {
                        MaterialTheme.colors.primary
                    } else {
                        null
                    },
                    addTappingSample = { currentButton, location, tapDurationMillis ->
                        tappingState.addTappingSample(currentButton, location, tapDurationMillis)
                    },
                    onFirstTap = tappingState::onFirstTap,
                    stopTimer = tappingState.timer::stopTimer,
                    startTimer = { tappingState.timer.startTimer(false) },
                    paused = paused
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        tappingState.cancel()
        super.onDestroyView()
        _binding = null
    }
}