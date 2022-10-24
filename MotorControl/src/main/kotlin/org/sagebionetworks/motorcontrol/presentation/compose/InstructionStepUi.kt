package org.sagebionetworks.motorcontrol.presentation.compose

import org.sagebionetworks.assessmentmodel.presentation.compose.BlackButton
import org.sagebionetworks.assessmentmodel.presentation.compose.CloseTopBar
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.R
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.presentation.theme.*

@Composable
internal fun InstructionStepUi(
    modifier: Modifier = Modifier,
    image: Drawable?,
    animations: ArrayList<Drawable>,
    currentImage: MutableState<Int>,
    flippedImage: Boolean,
    imageTintColor: Color?,
    title: String?,
    detail: String?,
    nextButtonText: String,
    next:()->Unit,
    previousButtonText: String,
    previous:()->Unit,
    close:()->Unit,
    hideClose: Boolean = false,
) {
    val imageModifier = if (flippedImage) {
        Modifier
            .fillMaxSize()
            .scale(-1F, 1F)
    } else {
        Modifier.fillMaxSize()
    }
    Box {
        Column(modifier = modifier.background(BackgroundGray)) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .weight(1f, false)) {
                if (image != null) {
                    SingleImageUi(
                        image = image,
                        imageModifier = imageModifier,
                        imageTintColor = imageTintColor)
                }
                if (animations.isNotEmpty()) {
                    AnimationImageUi(
                        animations = animations,
                        currentImage = currentImage,
                        imageTintColor = imageTintColor,
                        imageModifier = imageModifier)
                }
                title?.let { title ->
                    Text(
                        text = title,
                        style = titleText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )
                }
                detail?.let { detail ->
                    Text(
                        text = detail,
                        style = detailText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 10.dp)
                    )
                }
            }
            Row {
//                Spacer(modifier = Modifier.weight(1f))
//                BlackButton(
//                    onClick = previous,
//                    text = previousButtonText,
//                    modifier = modifier.padding(vertical = 10.dp)
//                )
                Spacer(modifier = Modifier.weight(1f))
                BlackButton(
                    onClick = next,
                    text = nextButtonText,
                    modifier = modifier.padding(vertical = 10.dp)
                )
//                Spacer(modifier = Modifier.weight(1f))
            }
        }
        if (!hideClose) {
            CloseTopBar(onCloseClicked = close)
        }
    }
}

@Preview
@Composable
private fun InstructionStepPreview() {
    SageSurveyTheme {
        InstructionStepUi(
            image = null,
            animations = ArrayList(),
            currentImage = mutableStateOf(0),
            flippedImage = false,
            imageTintColor = null,
            title = "Title",
            detail = "Details",
            nextButtonText = stringResource(R.string.start),
            next = {},
            previousButtonText = stringResource(R.string.back),
            previous = {},
            close = {},
            hideClose = false)
    }
}
