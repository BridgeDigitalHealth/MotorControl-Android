//
//  KoinModule.kt
//
//

package org.sagebionetworks

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.sagebionetworks.assessmentmodel.AssessmentRegistryProvider
import org.sagebionetworks.assessmentmodel.BranchNode
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragment
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragmentProvider
import org.sagebionetworks.assessmentmodel.serialization.EmbeddedJsonAssessmentRegistryProvider
import org.sagebionetworks.assessmentmodel.serialization.Serialization
import org.sagebionetworks.motorcontrol.presentation.MotorControlAssessmentFragment
import org.sagebionetworks.motorcontrol.serialization.motorControlModuleInfoSerializersModule

val motorControlModule = module {
    single<AssessmentRegistryProvider>(named("sage-motorcontrol")) {
        EmbeddedJsonAssessmentRegistryProvider(
            get(), "motorcontrol_assessment_registry",
            Json {
                serializersModule = Serialization.SerializersModule.default +
                        motorControlModuleInfoSerializersModule
            },
        )
    }

    single<AssessmentFragmentProvider?>(named("sage-motorcontrol")) {
        object : AssessmentFragmentProvider {
            override fun fragmentFor(branchNode: BranchNode): AssessmentFragment {
                return MotorControlAssessmentFragment()
            }
        }}
}