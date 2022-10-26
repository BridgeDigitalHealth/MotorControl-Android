package org.sagebionetworks.motorcontrol.presentation.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.sagebionetworks.motorcontrol.presentation.theme.detailText
import org.sagebionetworks.motorcontrol.presentation.theme.titleText

@Composable
fun StepBodyTextUi(title: String?, detail: String?, modifier: Modifier) {
    title?.let {
        Text(
            text = it,
            style = titleText,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 20.dp)
        )
    }
    detail?.let {
        Text(
            text = it,
            style = detailText,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        )
    }
}