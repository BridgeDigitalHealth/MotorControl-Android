package org.sagebionetworks.motorcontrol.resultObjects

import kotlinx.datetime.Instant

interface ResultData {
    /// The identifier associated with the task, step, or asynchronous action.
    val identifier: String

    /// The start date timestamp for the result.
    val startDate: Instant

    /// The end date timestamp for the result.
    val endDate: Instant

    /// The `deepCopy()` method is intended to allow copying a result to retain the previous result
    /// when revisiting an action. Since a class with get/set variables will use a pointer to the instance
    /// this allows results to either be structs *or* classes and allows collections of results to use
    /// mapping to deep copy their children.
    fun deepCopy(): ResultData
}