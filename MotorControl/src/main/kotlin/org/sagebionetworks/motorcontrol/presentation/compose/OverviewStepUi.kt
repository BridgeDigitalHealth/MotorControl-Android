package org.sagebionetworks.motorcontrol.presentation.compose

import org.sagebionetworks.assessmentmodel.presentation.compose.BlackButton
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
import org.sagebionetworks.assessmentmodel.presentation.compose.CloseTopBar
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.presentation.theme.*

@Composable
internal fun OverviewStepUi(
    modifier: Modifier = Modifier,
    image: Drawable?,
    animations: ArrayList<Drawable>,
    currentImage: MutableState<Int>,
    imageTintColor: Color?,
    title: String?,
    detail: String?,
    icons: List<Drawable>,
    nextButtonText: String,
    next:()->Unit,
    close:()->Unit,
    hideClose: Boolean = false,
    ) {
    Box {
        Column(modifier = modifier.background(BackgroundGray)) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(0), reverseScrolling = true)
                    .weight(1f, false)) {
                if (image != null) {
                    SingleImageUi(
                        image = image,
                        imageModifier = modifier.fillMaxSize(),
                        imageTintColor = imageTintColor)
                }
                if (animations.isNotEmpty()) {
                    AnimationImageUi(
                        animations = animations,
                        currentImage = currentImage,
                        imageTintColor = imageTintColor,
                        imageModifier = modifier.fillMaxSize()
                    )
                }
                StepBodyTextUi(title, detail, modifier)
                if(icons.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.icon_header),
                        style = iconHeaderText,
                        textAlign = TextAlign.Center,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(bottom = 30.dp)
                    )
                    Row(horizontalArrangement = Arrangement.Center) {
                        for(icon in icons) {
                            Column(
                                modifier = modifier
                                    .padding(horizontal = 15.dp)
                                    .weight(1f, fill = false),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                SingleImageUi(
                                    image = icon,
                                    imageModifier = modifier.padding(bottom = 15.dp),
                                    imageTintColor = imageTintColor
                                )
                                Text(
                                    // TODO: Add icon title here
                                    text = "LONG TEXT THAT NEEDS TO WRAP AROUND",
                                    style = iconTitleText,
                                    textAlign = TextAlign.Center,
                                    modifier = modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
            MotorControlBottomNavigation(
                onNextClicked = next,
                nextText = nextButtonText,
                backEnabled = false
            )
        }
        if (!hideClose) {
            CloseTopBar(onCloseClicked = close)
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
            currentImage = mutableStateOf(0),
            imageTintColor = null,
            title = "Title",
            detail = "Details",
            icons = listOf(),
            nextButtonText = stringResource(R.string.start),
            next = {},
            close = {},
            hideClose = false)
    }
}
