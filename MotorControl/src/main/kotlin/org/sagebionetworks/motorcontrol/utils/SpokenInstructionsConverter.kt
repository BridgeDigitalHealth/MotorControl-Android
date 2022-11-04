//
//  SpokenInstructionsConverter.kt
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