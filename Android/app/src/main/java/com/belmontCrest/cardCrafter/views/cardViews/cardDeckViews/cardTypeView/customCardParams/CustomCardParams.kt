package com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.customCardParams

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.Param
import com.belmontCrest.cardCrafter.model.FSProp
import com.belmontCrest.cardCrafter.model.TAProp
import com.belmontCrest.cardCrafter.model.TSProp
import com.belmontCrest.cardCrafter.model.TextProps
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.ContentIcons
import com.belmontCrest.cardCrafter.uiFunctions.CustomText
import com.belmontCrest.cardCrafter.views.cardViews.cardDeckViews.cardTypeView.ParamedView

@Composable
fun CardStringView(text: String, getUIStyle: GetUIStyle, modifier: Modifier = Modifier) {
    CustomText(
        text = text, getUIStyle = getUIStyle,
        props = TextProps(fs = FSProp.Font20, ts = TSProp.LargeTitle, ta = TAProp.Center),
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun StringListView(list: List<String>, getUIStyle: GetUIStyle) {
    list.forEachIndexed { index, it ->
        CardStringView(it, getUIStyle)
        if (index != list.lastIndex) {
            HorizontalDivider()
        }
    }
}

@Composable
fun HintStringView(text: String, getUIStyle: GetUIStyle, focusManager: FocusManager) {
    var isHintRevealed by rememberSaveable { mutableStateOf(false) }
    CardStringView(
        text = if (isHintRevealed) text else stringResource(R.string.hint_field),
        getUIStyle = getUIStyle,
        modifier = Modifier
            .clickable {
                isHintRevealed = !isHintRevealed
                focusManager.clearFocus()
            }
            .background(
                color = getUIStyle.choiceColor(),
                shape = RoundedCornerShape(8.dp)
            )
    )
}


@Composable
fun FrontChoiceListView(
    choices: List<String>, clickedChoice: MutableState<Char>, getUIStyle: GetUIStyle,
    focusManager: FocusManager
) {
    choices.forEachIndexed { index, choice ->
        val letter = index.toString()[0]
        FrontChoiceView(choice, clickedChoice.value, letter, getUIStyle) {
            clickedChoice.value = letter; focusManager.clearFocus()
        }
    }
}

@Composable
fun FrontChoiceView(
    choice: String, clickedChoice: Char, letter: Char, getUIStyle: GetUIStyle, onClick: () -> Unit
) {
    CardStringView(
        choice, getUIStyle, Modifier
            .padding(vertical = 4.dp)
            .clickable { onClick() }
            .background(
                color =
                    if (clickedChoice == letter) getUIStyle.pickedChoice()
                    else getUIStyle.choiceColor(),
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Composable
fun BackChoiceListView(
    choices: List<String>, clickedChoice: Char, correct: Char, getUIStyle: GetUIStyle
) {
    choices.forEachIndexed { index, choice ->
        val letter = index.toString()[0]
        BackChoiceView(
            choice = choice, clickedChoice = clickedChoice, correct = correct,
            letter = letter, getUIStyle = getUIStyle
        )
    }
}

@Composable
fun BackChoiceView(
    choice: String, clickedChoice: Char, correct: Char, letter: Char, getUIStyle: GetUIStyle
) {
    CardStringView(
        choice, getUIStyle, Modifier
            .padding(vertical = 4.dp)
            .background(
                color =
                    if (clickedChoice == letter &&
                        correct != clickedChoice
                    ) {
                        getUIStyle.pickedChoice()
                    } else if (
                        correct == letter &&
                        clickedChoice != correct
                    ) {
                        getUIStyle.correctChoice()
                    } else if (
                        clickedChoice == correct &&
                        correct == letter
                    ) {
                        getUIStyle.correctChoice()
                    } else {
                        getUIStyle.choiceColor()
                    },
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Composable
fun ParamPair(pair: Param.Pair, getUIStyle: GetUIStyle) {
    ParamedView(pair.first, getUIStyle)
    ParamedView(pair.second, getUIStyle)
}

@Composable
fun ImageView(uri: String) {
    AsyncImage(
        model = uri, contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun AudioPlayerButton(uriString: String, getUIStyle: GetUIStyle) {
    val ci = ContentIcons(getUIStyle)
    val context = LocalContext.current
    // Remember and prepare a MediaPlayer for this URI
    val mediaPlayer = remember(uriString) {
        MediaPlayer().apply {
            setDataSource(context, uriString.toUri())
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build()
            )
            prepare()
        }
    }
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uriString) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
        }
    }

    DisposableEffect(uriString) {
        onDispose { mediaPlayer.release() }
    }
    IconButton(
        onClick = {
            if (isPlaying) mediaPlayer.pause()
            else mediaPlayer.start()
            isPlaying = !isPlaying
        },
    ) {
        val playIcon = Icons.Filled.PlayArrow
        val pauseIcon = ImageVector.vectorResource(R.drawable.baseline_pause)
        ci.ContentIcon(
            if (isPlaying) pauseIcon else playIcon,
            if (isPlaying) "Pause audio" else "Play audio"
        )

    }
}