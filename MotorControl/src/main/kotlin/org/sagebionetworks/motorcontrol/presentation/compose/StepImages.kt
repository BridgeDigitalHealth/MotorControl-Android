//
//  StepImages.kt
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

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun SingleImageUi(
    image: Drawable,
    surveyTint: Color = Color.Transparent,
    imageModifier: Modifier,
    imageTintColor: Color?,
    alpha: Float = 1.0F) {
    Image(
        painter = rememberDrawablePainter(drawable = image),
        contentDescription = null,
        modifier = imageModifier.background(surveyTint),
        alpha = alpha,
        contentScale = ContentScale.FillHeight,
        colorFilter = if (imageTintColor != null) {
            ColorFilter.tint(
                color = imageTintColor,
                blendMode = BlendMode.Modulate
            )
        } else {
            null
        }
    )
}

@Composable
fun AnimationImageUi(
    animations: ArrayList<Drawable>,
    surveyTint: Color = Color(0xFF8FD6FF),
    currentImage: MutableState<Int>,
    imageTintColor: Color?,
    imageModifier: Modifier) {
    Image(
        // Logic within the getter for animations accounts for currentImage.value starting at -1
        painter = rememberDrawablePainter(drawable = animations[ if (currentImage.value >= 0) currentImage.value else 0 ]),
        contentDescription = null,
        modifier = imageModifier.background(surveyTint),
        contentScale = ContentScale.FillHeight,
        colorFilter = if (imageTintColor != null) {
            ColorFilter.tint(
                color = imageTintColor,
                blendMode = BlendMode.Modulate
            )
        } else {
            null
        }
    )
}