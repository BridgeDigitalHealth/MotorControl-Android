package org.sagebionetworks.motorcontrol.presentation.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.R
import org.sagebionetworks.assessmentmodel.presentation.compose.*
import org.sagebionetworks.assessmentmodel.presentation.ui.theme.*

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
                        surveyTint = Color(0xFF8FD6FF),
                        imageModifier = imageModifier,
                        imageTintColor = imageTintColor
                    )
                }
                if (animations.isNotEmpty()) {
                    AnimationImageUi(
                        animations = animations,
                        surveyTint = Color(0xFF8FD6FF),
                        currentImage = animationIndex,
                        imageTintColor = imageTintColor,
                        imageModifier = imageModifier
                    )
                }
                StepBodyTextUi(title, detail, modifier)
            }
            MotorControlBottomNavigation(
                onBackClicked = { assessmentViewModel?.goBackward() },
                onNextClicked = { assessmentViewModel?.goForward() },
                nextText = nextButtonText,
                backEnabled = assessmentViewModel?.assessmentNodeState?.allowBackNavigation() == true,)
        }
        val openDialog = remember { mutableStateOf(false) }
        assessmentViewModel?.let {
            PauseScreenDialog(
                showDialog = openDialog.value,
                assessmentViewModel = it,
            ) {
                openDialog.value = false
            }
        }
        PauseTopBar(
            onPauseClicked = { openDialog.value = true },
            onSkipClicked = { assessmentViewModel?.skip() },
            showSkip = false
        )
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
            detail = "Details",
            nextButtonText = stringResource(R.string.start)
        )
    }
}
