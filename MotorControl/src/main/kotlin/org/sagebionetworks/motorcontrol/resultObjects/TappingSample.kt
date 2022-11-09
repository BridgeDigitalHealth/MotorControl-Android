package org.sagebionetworks.motorcontrol.resultObjects

interface TappingSample {
    // System clock time for the sample. This will be the same timestamp marker across different files.
    val uptime: Long

    // A relative timestamp indicating the time of the tap event.
    //
    // The timestamp is relative to the value of `startDate` in the 'TappingResultObject` object that includes this
    // sample.
    val timestamp: Long?

    // The current step path.
    val stepPath: String

    // An enumerated value that indicates which button was tapped, if any.
    //
    // If the value of this property is `.None`, it indicates that the tap was near, but not inside, one
    // of the target buttons.
    val buttonIdentifier: TappingButtonIdentifier

    // The location of the tap within the step's view.
    //
    // The location coordinates are relative to a rectangle whose size corresponds to
    // the `stepViewSize` in the enclosing `TappingResultObject` object.
    val location: List<Float>

    // A duration of the tap event.
    //
    // The time interval (in seconds) between touch down and touch release events.
    val duration: Float
}

// Used to differentiate the tap location of a TappingSample
enum class TappingButtonIdentifier {
    Left, Right, None
}