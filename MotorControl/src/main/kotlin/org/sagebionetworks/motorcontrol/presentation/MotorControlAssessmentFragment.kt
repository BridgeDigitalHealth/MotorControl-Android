//
//  MotorControlAssessmentFragment.kt
//
//

package org.sagebionetworks.motorcontrol.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.sagebionetworks.assessmentmodel.*
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragment
import org.sagebionetworks.motorcontrol.serialization.*

class MotorControlAssessmentFragment: AssessmentFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Prevent landscape mode for all steps in Motor Control Assessments
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun getFragmentForStep(step: Step): Fragment {
        return when (step) {
            is OverviewStep -> OverviewStepFragment()
            is InstructionStep -> InstructionStepFragment()
            is CountdownStep -> CountdownStepFragment()
            is MotionSensorStepObject -> MotionSensorStepFragment()
            is TappingStepObject -> TappingStepFragment()
            else -> super.getFragmentForStep(step)
        }
    }
}