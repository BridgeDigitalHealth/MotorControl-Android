//
//  OverviewStepUi.kt
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sagebionetworks.motorcontrol.R
import org.sagebionetworks.assessmentmodel.presentation.compose.BottomNavigation
import org.sagebionetworks.assessmentmodel.presentation.compose.CloseTopBar
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.presentation.theme.*

@Composable
internal fun OverviewStepUi(
    modifier: Modifier = Modifier,
    image: Drawable?,
    animations: ArrayList<Drawable>,
    animationIndex: MutableState<Int>,
    imageTintColor: Color?,
    title: String?,
    detail: String?,
    icons: List<Pair<Drawable?, String?>>,
    nextButtonText: String,
    next:()->Unit,
    close:()->Unit,
    ) {
    Box {
        Column(modifier = Modifier.background(BackgroundGray)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(0), reverseScrolling = true)
                    .weight(1f, false)
            ) {
                if (image != null) {
                    SingleImageUi(
                        image = image,
                        surveyTint = ImageBackgroundColor,
                        imageModifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan),
                        imageTintColor = imageTintColor
                    )
                }
                if (animations.isNotEmpty()) {
                    AnimationImageUi(
                        animations = animations,
                        surveyTint = ImageBackgroundColor,
                        currentImage = animationIndex,
                        imageTintColor = imageTintColor,
                        imageModifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan)
                    )
                }
                StepBodyTextUi(title, detail, modifier)
                if (icons.isNotEmpty()) {
                    IconsUi(icons = icons, imageTintColor = imageTintColor)
                }
            }
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BottomNavigation(
                    onBackClicked = {},
                    onNextClicked = next,
                    nextText = nextButtonText,
                    backEnabled = false,
                    backVisible = false
                )
            }
        }
        CloseTopBar(onCloseClicked = close)
    }
}

@Composable
private fun IconsUi(
    icons: List<Pair<Drawable?, String?>>,
    imageTintColor: Color?
) {
    Text(
        text = stringResource(R.string.icon_header),
        style = iconHeaderText,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
    )
    Row(
        modifier = Modifier.padding(bottom = 15.dp),
        horizontalArrangement = Arrangement.Center) {
        for (icon in icons) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .weight(1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon.first?.let {
                    SingleImageUi(
                        image = it,
                        imageModifier = Modifier.padding(bottom = 20.dp),
                        imageTintColor = imageTintColor
                    )
                }
                icon.second?.let {
                    Text(
                        text = it,
                        style = iconTitleText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                }
            }
        }
    }

}

@Preview
@Composable
private fun OverviewStepPreview() {
    SageSurveyTheme {
        OverviewStepUi(
            image = null,
            animations = ArrayList(),
            animationIndex = mutableStateOf(0),
            imageTintColor = null,
            title = "Title",
            detail = "Details",
            icons = listOf(),
            nextButtonText = stringResource(R.string.start),
            next = {},
            close = {})
    }
}
