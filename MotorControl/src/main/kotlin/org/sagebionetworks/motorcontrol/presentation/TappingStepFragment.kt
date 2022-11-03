//
//  TappingStepFragment.kt
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

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.sagebionetworks.assessmentmodel.presentation.StepFragment
import org.sagebionetworks.assessmentmodel.presentation.databinding.ComposeQuestionStepFragmentBinding
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.SageSurveyTheme
import org.sagebionetworks.assessmentmodel.serialization.loadDrawable
import org.sagebionetworks.motorcontrol.navigation.HandSelection
import org.sagebionetworks.motorcontrol.navigation.hand
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer
import org.sagebionetworks.motorcontrol.presentation.compose.TappingStepUi
import org.sagebionetworks.motorcontrol.serialization.TappingStepObject
import org.sagebionetworks.motorcontrol.utils.SpokenInstructionsConverter

open class TappingStepFragment: StepFragment() {

    private var _binding: ComposeQuestionStepFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var step: TappingStepObject

    private var textToSpeech: TextToSpeech? = null

    private var timer: StepTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = nodeState.node as TappingStepObject
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = ComposeQuestionStepFragmentBinding.inflate(layoutInflater, container, false)
        val drawable = step.imageInfo?.loadDrawable(requireContext())
        val tint = step.imageInfo?.tint ?: false
        val spokenInstructions = SpokenInstructionsConverter.convertSpokenInstructions(
            step.spokenInstructions ?: mapOf(),
            step.duration.toInt(),
            stepViewModel.nodeState.parent?.node?.hand()?.name
        )
        textToSpeech = TextToSpeech(context) {
            textToSpeech?.speak(spokenInstructions[0], TextToSpeech.QUEUE_ADD, null, "")
        }
        binding.questionContent.setContent {
            val countdown: MutableState<Long> = remember { mutableStateOf(step.duration.toLong() * 1000) }
            val tapCount: MutableState<Int> = remember { mutableStateOf(0) }
            timer = StepTimer(
                countdown = countdown,
                stepDuration = step.duration,
                finished = assessmentViewModel::goForward,
                textToSpeech = textToSpeech,
                spokenInstructions = spokenInstructions
            )
            SageSurveyTheme {
                TappingStepUi(
                    assessmentViewModel = assessmentViewModel,
                    image = drawable,
                    flippedImage = stepViewModel.nodeState.parent?.node?.hand()
                            == HandSelection.RIGHT,
                    imageTintColor = if (tint) {
                        MaterialTheme.colors.primary
                    } else {
                        null
                    },
                    timer = timer,
                    duration = step.duration,
                    countdown = countdown,
                    tapCount = tapCount
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        textToSpeech?.shutdown()
        timer?.stopTimer()
        super.onDestroyView()
        _binding = null
    }
}