//
//  InstructionStepUi.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.R
import org.sagebionetworks.assessmentmodel.presentation.compose.BottomNavigation
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*
import org.sagebionetworks.motorcontrol.presentation.theme.ImageBackgroundColor

@Composable
internal fun InstructionStepUi(
    modifier: Modifier = Modifier,
    assessmentViewModel: AssessmentViewModel?,
    image: Drawable?,
    animations: ArrayList<Drawable>,
    animationIndex: MutableState<Int>,
    flippedImage: Boolean,
    imageTintColor: Color?,
    title: String?,
    subtitle: String?,
    detail: String?,
    nextButtonText: String) {
    val imageModifier = if (flippedImage) {
        Modifier
            .fillMaxSize()
            .scale(-1F, 1F)
    } else {
        Modifier.fillMaxSize()
    }
    Box {
        Column(modifier = Modifier.background(BackgroundGray)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .weight(1f, false)
            ) {
                if (image != null) {
                    SingleImageUi(
                        image = image,
                        surveyTint = ImageBackgroundColor,
                        imageModifier = imageModifier,
                        imageTintColor = imageTintColor
                    )
                }
                if (animations.isNotEmpty()) {
                    AnimationImageUi(
                        animations = animations,
                        surveyTint = ImageBackgroundColor,
                        currentImage = animationIndex,
                        imageTintColor = imageTintColor,
                        imageModifier = imageModifier
                    )
                }
                StepBodyTextUi(title, detail, subtitle, modifier)
            }
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                val backEnabled = assessmentViewModel?.assessmentNodeState?.allowBackNavigation() == true
                BottomNavigation(
                    onBackClicked = { assessmentViewModel?.goBackward() },
                    onNextClicked = { assessmentViewModel?.goForward() },
                    nextText = nextButtonText,
                    backEnabled = backEnabled,
                    backVisible = backEnabled
                )
            }
        }
        MotorControlPauseUi(assessmentViewModel = assessmentViewModel)
    }
}


@Preview
@Composable
private fun InstructionStepPreview() {
    SageSurveyTheme {
        InstructionStepUi(
            assessmentViewModel = null,
            image = null,
            animations = ArrayList(),
            animationIndex = mutableStateOf(0),
            flippedImage = false,
            imageTintColor = null,
            title = "Title",
            subtitle = "Subtitle",
            detail = "Details",
            nextButtonText = stringResource(R.string.start)
        )
    }
}
