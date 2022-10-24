package org.sagebionetworks.motorcontrol.presentation.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun SingleImageUi(image: Drawable, imageModifier: Modifier, imageTintColor: Color?) {
    Image(
        painter = rememberDrawablePainter(drawable = image),
        contentDescription = null,
        modifier = imageModifier,
        contentScale = ContentScale.FillWidth,
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
fun AnimationImageUi(animations: ArrayList<Drawable>,
                currentImage: MutableState<Int>,
                imageTintColor: Color?,
                imageModifier: Modifier) {
    Image(
        painter = rememberDrawablePainter(drawable = animations[currentImage.value]),
        contentDescription = null,
        modifier = imageModifier,
        contentScale = ContentScale.FillWidth,
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