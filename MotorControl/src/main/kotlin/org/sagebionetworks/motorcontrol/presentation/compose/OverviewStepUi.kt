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
                        surveyTint = Color(0xFF8FD6FF),
                        imageModifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan),
                        imageTintColor = imageTintColor
                    )
                }
                if (animations.isNotEmpty()) {
                    AnimationImageUi(
                        animations = animations,
                        surveyTint = Color(0xFF8FD6FF),
                        currentImage = animationIndex,
                        imageTintColor = imageTintColor,
                        imageModifier = Modifier
                            .fillMaxSize()
                            .background(Color.Cyan)
                    )
                }
                StepBodyTextUi(title, detail, modifier)
                if (icons.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.icon_header),
                        style = iconHeaderText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 30.dp)
                    )
                    Row(
                        modifier = modifier.padding(bottom = 15.dp),
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
                                        modifier = modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                    )
                                }
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
        CloseTopBar(onCloseClicked = close)
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
