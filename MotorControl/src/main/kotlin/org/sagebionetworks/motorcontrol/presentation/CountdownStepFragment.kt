package org.sagebionetworks.motorcontrol.presentation

import android.os.Bundle
import android.os.CountDownTimer
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
import kotlin.math.ceil

open class CountdownStepFragment: StepFragment() {

    private var _binding: ComposeQuestionStepFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var step: CountdownStep

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        step = nodeState.node as CountdownStep
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = ComposeQuestionStepFragmentBinding.inflate(layoutInflater, container, false)
        val hideClose = true
        binding.questionContent.setContent {
            //TODO: Need to figure out theming with compose -nbrown 2/17/22
            val countdown: MutableState<Int> = remember { mutableStateOf(step.duration.toInt()) }
            object: CountDownTimer((step.duration * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Prevents countdown from instantly decrementing
                    if (ceil((millisUntilFinished.toDouble() / 1000)) != step.duration) {
                        countdown.value -= 1
                    }
                }
                override fun onFinish() {
                    this.cancel()
                    assessmentViewModel.goForward()
                }
            }.start()

            SageSurveyTheme {
                CountdownStepUi(
                    duration = step.duration,
                    countdown = countdown,
                    close = { assessmentViewModel.cancel() },
                    hideClose = hideClose
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}