package org.sagebionetworks.motorcontrol.utils

import org.sagebionetworks.assessmentmodel.SpokenInstructionTiming

object SpokenInstructionsConverter {
    fun convertSpokenInstructions(
        spokenInstructions: Map<SpokenInstructionTiming, String>,
        duration: Int,
        currentHand: String? = ""
    ): MutableMap<Int, String> {
        val convertedInstructions: MutableMap<Int, String> = mutableMapOf()
        for (instruction in spokenInstructions) {
            val replacementString = instruction.value.replace("%@", currentHand ?: "")
                when (instruction.key){
                SpokenInstructionTiming.Keyword.Start ->
                    convertedInstructions[0] = replacementString
                SpokenInstructionTiming.Keyword.Halfway ->
                    convertedInstructions[duration.div(2)] = replacementString
                SpokenInstructionTiming.Keyword.Countdown ->
                    convertedInstructions[(duration - (duration.div(4)))] = replacementString
                SpokenInstructionTiming.Keyword.End ->
                    convertedInstructions[duration] = replacementString
                else -> {
                    convertedInstructions[duration - instruction.key.name.toDouble().toInt()] = replacementString
                }
            }
        }
        return convertedInstructions
    }
}