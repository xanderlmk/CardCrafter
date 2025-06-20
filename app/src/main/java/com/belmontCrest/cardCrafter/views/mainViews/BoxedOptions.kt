package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.ui.theme.generalSettingsOptionsModifier
import com.belmontCrest.cardCrafter.uiFunctions.EditIntField
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton

@Composable
fun ExpandedIntBoxedOptions(
    onClick: () -> Unit, getUIStyle: GetUIStyle, string: MutableState<String>,
    success: Boolean, text: String
) {
    Column(
        modifier = Modifier.generalSettingsOptionsModifier(getUIStyle.getColorScheme()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            fontStyle = FontStyle.Italic,
            color = getUIStyle.titleColor(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            EditIntField(
                value = string.value,
                onValueChanged = { string.value = it },
                labelStr = text,
                modifier = Modifier
                    .fillMaxWidth(0.30f)
                    .padding(vertical = 1.dp, horizontal = 2.dp)
            )
            SubmitButton(
                onClick = { onClick() }, enabled = !success,
                modifier = Modifier
                    .fillMaxWidth(0.44f)
                    .padding(vertical = 1.dp, horizontal = 2.dp),
                getUIStyle = getUIStyle,
                string = stringResource(R.string.save),
                textColor = getUIStyle.titleColor()
            )
        }
    }
}