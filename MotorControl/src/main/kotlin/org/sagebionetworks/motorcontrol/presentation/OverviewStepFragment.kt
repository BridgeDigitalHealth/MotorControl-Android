//
//  OverviewStepFragment.kt
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
                    subtitle = step.subtitle,
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