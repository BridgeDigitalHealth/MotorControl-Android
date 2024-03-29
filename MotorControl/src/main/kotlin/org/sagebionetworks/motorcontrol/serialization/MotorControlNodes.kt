//
//  MotorControlNodes.kt
//
//

package org.sagebionetworks.motorcontrol.serialization

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.sagebionetworks.assessmentmodel.*
import org.sagebionetworks.assessmentmodel.navigation.BranchNodeState
import org.sagebionetworks.assessmentmodel.navigation.Navigator
import org.sagebionetworks.assessmentmodel.navigation.NodeNavigator
import org.sagebionetworks.assessmentmodel.serialization.BaseActiveStepObject
import org.sagebionetworks.assessmentmodel.serialization.NodeContainerObject
import org.sagebionetworks.assessmentmodel.serialization.StepObject
import org.sagebionetworks.motorcontrol.navigation.TwoHandNavigator

val motorControlNodeSerializersModule = SerializersModule {
    polymorphic(Node::class) {
        subclass(TwoHandAssessmentObject::class)
        subclass(WalkAssessmentObject::class)
        subclass(HandInstructionStepObject::class)
        subclass(TremorStepObject::class)
        subclass(TappingStepObject::class)
        subclass(WalkStepObject::class)
        subclass(BalanceStepObject::class)
    }
    polymorphic(Assessment::class) {
        subclass(TwoHandAssessmentObject::class)
        subclass(WalkAssessmentObject::class)
    }
    polymorphic(Result::class) {
        subclass(TappingResultObject::class)
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

@Serializable
@SerialName("walkAssessment")
data class WalkAssessmentObject(
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
        return NodeNavigator(this)
    }
    override fun createResult(): AssessmentResult = super<Assessment>.createResult()
    override fun unpack(originalNode: Node?, moduleInfo: ModuleInfo, registryProvider: AssessmentRegistryProvider): WalkAssessmentObject {
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
abstract class MotionSensorStepObject : BaseActiveStepObject() {
}

@Serializable
@SerialName("tremor")
data class TremorStepObject(
    override val duration: Double,
    override val identifier: String
) : MotionSensorStepObject()

@Serializable
@SerialName("walk")
data class WalkStepObject(
    override val duration: Double,
    override val identifier: String
) : MotionSensorStepObject()

@Serializable
@SerialName("balance")
data class BalanceStepObject(
    override val duration: Double,
    override val identifier: String
) : MotionSensorStepObject()

@Serializable
@SerialName("tapping")
data class TappingStepObject(
    override val duration: Double,
    override val identifier: String
) : BaseActiveStepObject() {
    override fun createResult(): TappingResultObject {
        return TappingResultObject(identifier)
    }
}

@Serializable
@SerialName("tapping")
data class TappingResultObject(
    override val identifier: String,
    @SerialName("startDate")
    override var startDateTime: Instant = Clock.System.now(),
    @SerialName("endDate")
    override var endDateTime: Instant? = null,
    var hand: String? = null,
    var buttonRectLeft: String = "",
    var buttonRectRight: String = "",
    var samples: List<TappingSampleObject> = mutableListOf(),
    var tapCount: Int = 0
    ) : JsonFileArchivableResult {
    override fun copyResult(identifier: String): TappingResultObject {
        return this.copy(samples = this.samples.map { it.copy() })
    }

    override fun getJsonArchivableFile(stepPath: String): JsonArchivableFile {
        return JsonArchivableFile(
            filename = "${hand}_$identifier",
            json = JsonCoder.default.encodeToString(this as Result),
            jsonSchema = "https://sage-bionetworks.github.io/mobile-client-json/schemas/v2/TappingResultObject.json"
        )
    }
}

@Serializable
data class TappingSampleObject(
    val uptime: Double,
    val timestamp: Double?,
    val stepPath: String,
    val buttonIdentifier: String,
    val location: List<Float>,
    val duration: Double
)

// Used to differentiate the button tapped for a TappingSample
enum class TappingButtonIdentifier {
    Left, Right, None
}