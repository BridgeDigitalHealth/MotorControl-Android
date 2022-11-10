package org.sagebionetworks.motorcontrol.resultObjects

import org.sagebionetworks.assessmentmodel.Result
import org.sagebionetworks.motorcontrol.serialization.TappingSampleObject

interface TappingResult: Result {
    override fun copyResult(identifier: String) : TappingResult
    var hand: String?
    var samples: List<TappingSampleObject>
    var tapCount: Int
}