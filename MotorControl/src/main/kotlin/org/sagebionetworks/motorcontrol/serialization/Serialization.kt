//
//  Serialization.kt
//
//

package org.sagebionetworks.motorcontrol.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import org.sagebionetworks.assessmentmodel.EmbeddedJsonModuleInfo
import org.sagebionetworks.assessmentmodel.JsonModuleInfo
import org.sagebionetworks.assessmentmodel.TransformableAssessment
import org.sagebionetworks.assessmentmodel.resourcemanagement.ResourceInfo
import org.sagebionetworks.assessmentmodel.serialization.*
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object JsonCoder {
    val default = Json {
        serializersModule = motorControlNodeSerializersModule +
                Serialization.SerializersModule.default
        ignoreUnknownKeys = true
        isLenient = true
    }
}

val motorControlModuleInfoSerializersModule = SerializersModule {
    polymorphic(JsonModuleInfo::class) {
        subclass(MotorControlModuleInfoObject::class)
    }
}

@Serializable
@SerialName("MotorControlModuleInfo")
data class MotorControlModuleInfoObject(
    override val assessments: List<TransformableAssessment>,
    override var packageName: String? = null,
    override val bundleIdentifier: String? = null): ResourceInfo, EmbeddedJsonModuleInfo {

    override val resourceInfo: ResourceInfo
        get() = this
    override val jsonCoder: Json
        get() {
            return JsonCoder.default
        }

    @Transient
    override var decoderBundle: Any? = null
}