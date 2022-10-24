package org.sagebionetworks.motorcontrol.presentation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.assessmentmodel.CompletionStep
import org.sagebionetworks.assessmentmodel.ContentNodeStep
import org.sagebionetworks.assessmentmodel.OverviewStep
import org.sagebionetworks.assessmentmodel.presentation.StepFragment
import org.sagebionetworks.assessmentmodel.presentation.databinding.ComposeQuestionStepFragmentBinding
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.SageSurveyTheme
import org.sagebionetworks.assessmentmodel.serialization.AnimatedImage
import org.sagebionetworks.assessmentmodel.serialization.FetchableImage
import org.sagebionetworks.assessmentmodel.serialization.loadDrawable
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.navigation.hand
import org.sagebionetworks.motorcontrol.presentation.compose.AnimationTimer
import org.sagebionetworks.motorcontrol.presentation.compose.CompletionStepUi
import org.sagebionetworks.motorcontrol.presentation.compose.InstructionStepUi

open class InstructionStepFragment: StepFragment() {

    private var _binding: ComposeQuestionStepFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var step: ContentNodeStep

    private lateinit var animationTimer: AnimationTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = nodeState.node as ContentNodeStep
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = ComposeQuestionStepFragmentBinding.inflate(layoutInflater, container, false)
        val drawable = step.imageInfo?.loadDrawable(requireContext())
        val drawables = ArrayList<Drawable>()
        val animatedImageInfo = step.imageInfo as? AnimatedImage
        val currentImage: MutableState<Int> = mutableStateOf(0)
        animatedImageInfo?.let { imageInfo ->
            for (animatedImage in imageInfo.imageNames) {
                val fetchable = FetchableImage(animatedImage).loadDrawable(requireContext())
                fetchable?.let {
                    drawables.add(fetchable) }
            }
            animationTimer = AnimationTimer(
                animatedImageInfo.imageNames.size,
                animatedImageInfo.animationDuration,
                animatedImageInfo.animationRepeatCount ?: 1000,
                currentImage
            )
        }
        val tint = step.imageInfo?.tint ?: false
        var hideClose = false
        val buttonTextResource = when (step) {
            is OverviewStep -> {
                R.string.start
            }
            is CompletionStep -> {
                hideClose = true
                R.string.exit
            }
            else -> {
                R.string.next
            }
        }
        binding.questionContent.setContent {
            //TODO: Need to figure out theming with compose -nbrown 2/17/22
            SageSurveyTheme {
                if (step is CompletionStep) {
                    CompletionStepUi(
                        title = step.title ?: getString(R.string.well_done),
                        detail = step.detail ?: getString(R.string.thank_you_for),
                        nextButtonText = stringResource(buttonTextResource),
                        next = { assessmentViewModel.goForward() }
                    )
                } else {
                    InstructionStepUi(
                        image = if(drawables.isEmpty()) drawable else null,
                        animations = drawables,
                        currentImage = currentImage,
                        flippedImage = stepViewModel.nodeState.parent?.node?.hand()
                                == HandSelection.RIGHT,
                        imageTintColor = if (tint) {
                            MaterialTheme.colors.primary
                        } else {
                            null
                        },
                        title = step.title?.replace("%@",
                            stepViewModel.nodeState.parent?.node?.hand()?.name ?: ""),
                        detail = step.detail?.replace("%@",
                            stepViewModel.nodeState.parent?.node?.hand()?.name ?: ""),
                        nextButtonText = stringResource(R.string.next),
                        next = {
                            assessmentViewModel.goForward()
                            this.animationTimer.stop()
                        },
                        previousButtonText = stringResource(R.string.back),
                        previous = {
                            assessmentViewModel.goBackward()
                            this.animationTimer.stop()
                        },
                        close = {
                            assessmentViewModel.cancel()
                            this.animationTimer.stop()
                        },
                        hideClose = hideClose
                    )
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}