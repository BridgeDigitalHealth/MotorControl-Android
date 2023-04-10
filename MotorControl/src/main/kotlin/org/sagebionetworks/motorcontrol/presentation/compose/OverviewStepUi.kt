//
//  OverviewStepUi.kt
//
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
import androidx.compose.runtime.remember
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
    imageName: String,
    animations: ArrayList<Drawable>,
    animationIndex: MutableState<Int>,
    imageTintColor: Color?,
    title: String?,
    subtitle: String?,
    detail: String?,
    icons: List<Pair<Drawable?, String?>>,
    iconNames: ArrayList<String?>,
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
                        imageName = imageName,
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
                        firstImageName = imageName,
                        surveyTint = ImageBackgroundColor,
                        currentImage = animationIndex,
                        imageTintColor = imageTintColor,
                        imageModifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan)
                    )
                }
                StepBodyTextUi(title, detail, subtitle, modifier)
                if (icons.isNotEmpty()) {
                    IconsUi(icons = icons, iconNames = iconNames, imageTintColor = imageTintColor)
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
    iconNames: List<String?>,
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
        for (iconIndex in icons.indices) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .weight(1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icons[iconIndex].first?.let {
                    SingleImageUi(
                        image = it,
                        imageName = iconNames[iconIndex] ?: "IMAGE",
                        imageModifier = Modifier.padding(bottom = 20.dp),
                        imageTintColor = imageTintColor
                    )
                }
                icons[iconIndex].second?.let {
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
            imageName = "IMAGE",
            animations = ArrayList(),
            animationIndex = remember { mutableStateOf(0) },
            imageTintColor = null,
            title = "Title",
            subtitle = "Subtitle",
            detail = "Details",
            icons = listOf(),
            iconNames = arrayListOf(),
            nextButtonText = stringResource(R.string.start),
            next = {},
            close = {})
    }
}
