//
//  SpokenInstructionsConverter.kt
//
//

package org.sagebionetworks.motorcontrol.utils

import org.sagebionetworks.assessmentmodel.SpokenInstructionTiming

// This object is used to convert the SpokenInstructionTiming Keywords to Ints
object SpokenInstructionsConverter {
    fun convertSpokenInstructions(
        spokenInstructions: Map<SpokenInstructionTiming, String>?,
        duration: Int,
        currentHand: String? = ""
    ): MutableMap<Int, String> {
        val convertedInstructions: MutableMap<Int, String> = mutableMapOf()
        spokenInstructions?.let {
            for (instruction in it) {
                val replacementString = instruction.value.replace("%@", currentHand ?: "")
                when (instruction.key){
                    SpokenInstructionTiming.Keyword.Start ->
                        convertedInstructions[0] = replacementString
                    SpokenInstructionTiming.Keyword.Halfway ->
                        convertedInstructions[duration.floorDiv(2)] = replacementString
                    SpokenInstructionTiming.Keyword.Countdown ->
                        convertedInstructions[duration.floorDiv(4)] = replacementString
                    SpokenInstructionTiming.Keyword.End ->
                        convertedInstructions[duration] = replacementString
                    else -> {
                        convertedInstructions[instruction.key.name.toDouble().toInt()] = replacementString
                    }
                }
            }
        }
        return convertedInstructions
    }
}