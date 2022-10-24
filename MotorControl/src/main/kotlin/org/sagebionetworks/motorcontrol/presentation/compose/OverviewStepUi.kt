package org.sagebionetworks.motorcontrol.presentation.compose

import org.sagebionetworks.assessmentmodel.presentation.compose.BlackButton
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
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
                            .padding(bottom = 20.dp)
                    )
                }
                if(icons.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.icon_header),
                        style = iconText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 20.dp)
                    )
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        for(icon in icons) {
                            Image(
                                painter = rememberDrawablePainter(drawable = icon),
                                contentDescription = null,
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
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Row {
                Spacer(modifier = Modifier.weight(1f))
                BlackButton(
                    onClick = next,
                    text = nextButtonText,
                    modifier = modifier.padding(vertical = 10.dp)
                )
            }
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
