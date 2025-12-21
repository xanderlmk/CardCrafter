package com.belmontCrest.cardCrafter.views.mainViews

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.ui.GetUIStyle
import com.belmontCrest.cardCrafter.ui.functions.ColumnContentWithBorder

@Composable
fun DefaultDeckOptions(
    changeReviewAmount: () -> Unit,
    changeCardAmount: () -> Unit,
    reviewAmount: MutableState<String>,
    cardAmount: MutableState<String>,
    reviewSuccess: Boolean,
    cardSuccess: Boolean,
    getUIStyle: GetUIStyle
) {
    ColumnContentWithBorder(getUIStyle, "Default Deck Options") {
        ExpandedIntBoxedOptions(
            onClick = changeReviewAmount, getUIStyle = getUIStyle,
            string = reviewAmount, success = reviewSuccess,
            text = stringResource(R.string.review_amount)
        )
        ExpandedIntBoxedOptions(
            onClick = changeCardAmount, getUIStyle = getUIStyle,
            string = cardAmount, success = cardSuccess,
            text = stringResource(R.string.card_amount)
        )
    }
}