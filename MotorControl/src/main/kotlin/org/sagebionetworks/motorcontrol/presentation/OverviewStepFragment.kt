package org.sagebionetworks.motorcontrol.presentation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import org.sagebionetworks.assessmentmodel.ButtonAction
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.assessmentmodel.presentation.StepFragment
import org.sagebionetworks.assessmentmodel.presentation.databinding.ComposeQuestionStepFragmentBinding
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.SageSurveyTheme
import org.sagebionetworks.assessmentmodel.serialization.AnimatedImage
import org.sagebionetworks.assessmentmodel.serialization.FetchableImage
import org.sagebionetworks.assessmentmodel.serialization.OverviewStepObject
import org.sagebionetworks.assessmentmodel.serialization.loadDrawable
import org.sagebionetworks.motorcontrol.presentation.compose.AnimationTimer
import org.sagebionetworks.motorcontrol.presentation.compose.OverviewStepUi

open class OverviewStepFragment: StepFragment() {

    private var _binding: ComposeQuestionStepFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var step: OverviewStepObject

    private var animationTimer: AnimationTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = nodeState.node as OverviewStepObject
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = ComposeQuestionStepFragmentBinding.inflate(layoutInflater, container, false)
        val drawable = step.imageInfo?.loadDrawable(requireContext())
        val drawables = ArrayList<Drawable>()
        val animatedImageInfo = step.imageInfo as? AnimatedImage
        val animationIndex: MutableState<Int> = mutableStateOf(0)
        animatedImageInfo?.let { imageInfo ->
            for (animatedImage in imageInfo.imageNames) {
                FetchableImage(animatedImage).loadDrawable(requireContext())?.let {
                    drawables.add(it) }
            }
            animationTimer = AnimationTimer(
                imageInfo.imageNames.size,
                imageInfo.animationDuration,
                imageInfo.animationRepeatCount,
                animationIndex
            )
        }
        val tint = step.imageInfo?.tint ?: false
        val icons = ArrayList<Pair<Drawable?, String?>>()
        for(iconIndex in 0 until (step.icons?.size ?: 0)) {
            step.icons?.get(iconIndex).let {
                icons.add(Pair(it?.loadDrawable(requireContext()), it?.label))
            }
        }
        val buttonTextResource = stepViewModel.nodeState.node
            .buttonMap[ButtonAction.valueOf("goForward")]?.buttonTitle
        binding.questionContent.setContent {
            //TODO: Need to figure out theming with compose -nbrown 2/17/22
            SageSurveyTheme {
                OverviewStepUi(
                    image = if (drawables.isEmpty()) drawable else null,
                    animations = drawables,
                    animationIndex = animationIndex,
                    imageTintColor = if (tint) {
                        MaterialTheme.colors.primary
                    } else {
                        null
                    },
                    title = step.title,
                    detail = step.detail,
                    icons = icons,
                    nextButtonText = buttonTextResource ?: stringResource(R.string.start),
                    next = {
                        assessmentViewModel.goForward()
                    },
                    close = {
                        assessmentViewModel.cancel()
                    }
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        animationTimer?.stop()
        super.onDestroyView()
        _binding = null
    }
}