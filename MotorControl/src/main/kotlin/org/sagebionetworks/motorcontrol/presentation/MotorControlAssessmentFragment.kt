package org.sagebionetworks.motorcontrol.presentation

import androidx.fragment.app.Fragment
import org.sagebionetworks.assessmentmodel.*
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragment
import org.sagebionetworks.assessmentmodel.presentation.DebugStepFragment
import org.sagebionetworks.assessmentmodel.presentation.InstructionStepFragment
import org.sagebionetworks.assessmentmodel.presentation.SurveyQuestionStepFragment
import org.sagebionetworks.assessmentmodel.survey.Question

class MotorControlAssessmentFragment: AssessmentFragment() {
    override fun getFragmentForStep(step: Step): Fragment {
        return when (step) {
            is Question -> SurveyQuestionStepFragment()
            is CountdownStep -> CountdownStepFragment()
            is InstructionStep -> InstructionStepFragment()
            is CompletionStep -> InstructionStepFragment()
//            is OverviewStep -> OverviewStepFragment()
            else -> DebugStepFragment()
        }
    }
}