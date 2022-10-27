//
//  MotorControlBottomNavigation.kt
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

package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.compose.BlackButton
import org.sagebionetworks.assessmentmodel.presentation.compose.BlackNextButton
import org.sagebionetworks.assessmentmodel.presentation.compose.WhiteBackButton

@Composable
fun MotorControlBottomNavigation(
    onBackClicked: () -> Unit = {},
    onNextClicked: () -> Unit,
    nextText: String? = null,
    backEnabled: Boolean = true,
    nextEnabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        if (backEnabled) {
            WhiteBackButton(onClick = onBackClicked)
        }
        Spacer(modifier = Modifier.weight(1f))
        if (nextText != null) {
            BlackButton(onClick = onNextClicked, enabled = nextEnabled, text = nextText)
        } else {
            BlackNextButton(onClick = onNextClicked, enabled = nextEnabled)
        }
    }
}