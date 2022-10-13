package org.sagebionetworks.motorcontrol.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.sagebionetworks.assessmentmodel.*
import org.sagebionetworks.assessmentmodel.navigation.BranchNodeState
import org.sagebionetworks.assessmentmodel.navigation.Navigator
import org.sagebionetworks.assessmentmodel.serialization.BaseActiveStepObject
import org.sagebionetworks.assessmentmodel.serialization.NodeContainerObject
import org.sagebionetworks.assessmentmodel.serialization.StepObject
import org.sagebionetworks.motorcontrol.navigation.TwoHandNavigator

val motorControlNodeSerializersModule = SerializersModule {
    polymorphic(Node::class) {
        subclass(TwoHandAssessmentObject::class)
        subclass(HandInstructionStepObject::class)
        subclass(TremorStepObject::class)
        subclass(TappingStepObject::class)
    }
}

@Serializable
@SerialName("twoHandAssessment")
data class TwoHandAssessmentObject(
    override val identifier: String,
    @SerialName("steps")
    override val children: List<Node>,
    override val guid: String? = null,
    override val versionString: String? = null,
    override val schemaIdentifier: String? = null,
    override var estimatedMinutes: Int = 0,
    @SerialName("asyncActions")
    override val asyncActions: List<AsyncActionConfiguration> = listOf(),
    override val copyright: String? = null,
    @SerialName("\$schema")
    override val schema: String? = null,
    override val interruptionHandling: InterruptionHandlingObject = InterruptionHandlingObject(),
) : NodeContainerObject(), Assessment, AsyncActionContainer {
    override fun createNavigator(nodeState: BranchNodeState): Navigator {
        return TwoHandNavigator(this)
    }
    override fun createResult(): AssessmentResult = super<Assessment>.createResult()
    override fun unpack(originalNode: Node?, moduleInfo: ModuleInfo, registryProvider: AssessmentRegistryProvider): TwoHandAssessmentObject {
        super<Assessment>.unpack(originalNode, moduleInfo, registryProvider)
        val copyChildren = children.map {
            it.unpack(null, moduleInfo, registryProvider)
        }
        val identifier = originalNode?.identifier ?: this.identifier
        val guid = originalNode?.identifier ?: this.guid
        val copy = copy(identifier = identifier, guid = guid, children = copyChildren)
        copy.copyFrom(this)
        return copy
    }

}

/**
 * Information steps
 */
@Serializable
@SerialName("handInstruction")
data class HandInstructionStepObject(
    override val identifier: String,
    @SerialName("image")
    override var imageInfo: ImageInfo? = null,
    override var fullInstructionsOnly: Boolean = false
) : StepObject(), InstructionStep

/**
 * Active steps
 */
@Serializable
@SerialName("tremor")
data class TremorStepObject(
    override val duration: Double,
    override val identifier: String
) : BaseActiveStepObject(), ActiveStep

@Serializable
@SerialName("tapping")
data class TappingStepObject(
    override val duration: Double,
    override val identifier: String
) : BaseActiveStepObject(), ActiveStep