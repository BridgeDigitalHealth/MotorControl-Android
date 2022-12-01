//
//  MotorControlNodes.kt
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
    polymorphic(Assessment::class) {
        subclass(TwoHandAssessmentObject::class)
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
) : BaseActiveStepObject(), ActiveStep {
    override fun createResult(): TappingResultObject {
        return TappingResultObject(identifier)
    }
}

@Serializable
@SerialName("tapping")
data class TappingResultObject(
    override val identifier: String,
    override var startDateTime: Instant = Clock.System.now(),
    override var endDateTime: Instant? = null,
    var hand: String? = null,
    var samples: List<TappingSampleObject> = mutableListOf(),
    var tapCount: Int = 0
    ) : JsonFileArchivableResult {
    override fun copyResult(identifier: String): TappingResultObject {
        return this.copy(samples = this.samples.map { it.copy() })
    }

    override fun getJsonArchivableFile(stepPath: String): JsonArchivableFile {
        return JsonArchivableFile(
            filename = "${hand}_$identifier",
            json = Json.encodeToString(this),
            jsonSchema = "https://sage-bionetworks.github.io/mobile-client-json/schemas/v2/MotionRecord.json"
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