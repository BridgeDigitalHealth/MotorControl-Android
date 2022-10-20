package com.example.motorcontrol_android

import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.sagebionetworks.assessmentmodel.AssessmentRegistryProvider
import org.sagebionetworks.assessmentmodel.BranchNode
import org.sagebionetworks.assessmentmodel.RootAssessmentRegistryProvider
import org.sagebionetworks.assessmentmodel.navigation.CustomNodeStateProvider
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragment
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragmentProvider
import org.sagebionetworks.assessmentmodel.resourcemanagement.FileLoader
import org.sagebionetworks.assessmentmodel.serialization.FileLoaderAndroid
import org.sagebionetworks.motorcontrol.presentation.MotorControlAssessmentFragment

val appModule = module {

    single<AssessmentRegistryProvider>() {
        RootAssessmentRegistryProvider(get(), listOf(
            get(qualifier = named("sage-motorcontrol")))
        )

    }
    factory<FileLoader> {FileLoaderAndroid(get())}

    single<CustomNodeStateProvider?> {
        object : CustomNodeStateProvider {

        }}

    single<AssessmentFragmentProvider?> {
        object : AssessmentFragmentProvider {
            override fun fragmentFor(branchNode: BranchNode): AssessmentFragment {
                return MotorControlAssessmentFragment()
            }
        }}

}