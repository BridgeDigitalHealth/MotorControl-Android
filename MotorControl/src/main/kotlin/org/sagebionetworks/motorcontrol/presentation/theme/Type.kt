//
//  Type.kt
//
//

package org.sagebionetworks.motorcontrol.presentation.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.sagebionetworks.motorcontrol.R

val fonts = FontFamily(
    Font(R.font.lato_bold, weight = FontWeight.SemiBold, style = FontStyle.Normal),
    Font(R.font.lato_bold_italic, weight = FontWeight.SemiBold, style = FontStyle.Italic),
    Font(R.font.lato_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(R.font.lato_light, weight = FontWeight.Light, style = FontStyle.Normal),
    Font(R.font.lato_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(R.font.lato_regular, weight = FontWeight.Normal, style = FontStyle.Normal),
)

val detailText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp
)
val iconHeaderText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp
)
val iconTitleText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp
)
val titleText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 36.sp
)
val dialText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 75.sp
)
val dialSecondaryText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 25.sp
)
val countdownBeginText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Bold,
    fontSize = 25.sp
)
val tapButtonText = TextStyle(
    fontFamily = fonts,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp
)
