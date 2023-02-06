//
//  CountdownStepFragment.kt
//
//

package org.sagebionetworks.motorcontrol.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.sagebionetworks.motorcontrol.presentation.compose.CountdownStepUi
import org.sagebionetworks.assessmentmodel.CountdownStep
import org.sagebionetworks.assessmentmodel.presentation.StepFragment
import org.sagebionetworks.assessmentmodel.presentation.databinding.ComposeQuestionStepFragmentBinding
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.SageSurveyTheme
import org.sagebionetworks.motorcontrol.presentation.compose.StepTimer

open class CountdownStepFragment: StepFragment() {

    private var _binding: ComposeQuestionStepFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var step: CountdownStep

    private var timer: StepTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = nodeState.node as CountdownStep
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = ComposeQuestionStepFragmentBinding.inflate(layoutInflater, container, false)
        binding.questionContent.setContent {
            val countdown: MutableState<Long> = remember { mutableStateOf(step.duration.toLong() * 1000) }
            timer = StepTimer(
                countdown,
                step.duration,
                assessmentViewModel::goForward
            )
            timer?.startTimer()
            SageSurveyTheme {
                CountdownStepUi(
                    assessmentViewModel = assessmentViewModel,
                    duration = step.duration,
                    countdown = countdown,
                    timer = timer
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        timer?.clear()
        super.onDestroyView()
        _binding = null
    }
}