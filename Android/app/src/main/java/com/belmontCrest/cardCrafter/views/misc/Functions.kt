package com.belmontCrest.cardCrafter.views.misc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.belmontCrest.cardCrafter.R
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.belmontCrest.cardCrafter.controller.cardHandlers.returnReviewsLeft
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.AddCardViewModel
import com.belmontCrest.cardCrafter.controller.view.models.cardViewsModels.EditCardViewModel
import com.belmontCrest.cardCrafter.model.ui.states.CDetails
import com.belmontCrest.cardCrafter.model.ui.states.SelectedKeyboard
import com.belmontCrest.cardCrafter.ui.theme.GetUIStyle
import com.belmontCrest.cardCrafter.uiFunctions.buttons.SubmitButton
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.core.graphics.scale
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.customCardInit.MiddleParam
import com.belmontCrest.cardCrafter.model.ui.states.SealedAllCTs
import com.belmontCrest.cardCrafter.uiFunctions.LatexKeyboard
import com.belmontCrest.cardCrafter.uiFunctions.katex.menu.KaTeXMenu
import com.belmontCrest.cardCrafter.uiFunctions.showToastMessage

@Composable
fun StepListView(
    steps: List<String>, onAddStep: () -> Unit, onValueChanged: (Int, String) -> Unit,
    enabled: Boolean, onIdle: () -> Unit, onRemoveStep: () -> Unit, onFocusChanged: (Int) -> Unit,
    getUIStyle: GetUIStyle, selectedSymbol: KaTeXMenu, selectedKB: SelectedKeyboard?,
    onLostFocus: () -> Unit = {}, isCustomCard: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (steps.isEmpty()) {
            SubmitButton(
                onClick = onAddStep, enabled = enabled, modifier = Modifier.padding(8.dp),
                getUIStyle = getUIStyle, string = "Add a step"
            )
        } else {
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    LatexKeyboard(
                        value = step,
                        onValueChanged = { newText -> onValueChanged(index, newText) },
                        labelStr = "Step: ${index + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        onFocusChanged = { onFocusChanged(index) },
                        onLostFocus = onLostFocus,
                        kt = selectedSymbol,
                        onIdle = onIdle,
                        selectedKB = selectedKB,
                        actualKB = SelectedKeyboard.Step(index)
                    )
                }
            }

            SubmitButton(
                onClick = onAddStep, enabled = enabled, modifier = Modifier.padding(8.dp),
                getUIStyle = getUIStyle, string = "Add a step"
            )
            if (!isCustomCard || steps.size > 1) {
                SubmitButton(
                    onClick = onRemoveStep,
                    enabled = enabled,
                    modifier = Modifier.padding(top = 4.dp),
                    getUIStyle = getUIStyle,
                    string = "Remove step"
                )
            }
        }
    }
}


@Composable
fun ImagePicker(getUIStyle: GetUIStyle, onImagePicked: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let(onImagePicked)
    }
    SubmitButton(
        onClick = { launcher.launch("image/*") },
        enabled = true, getUIStyle = getUIStyle, string = "Choose Image",
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun AudioPicker(getUIStyle: GetUIStyle, onAudioPicked: (Uri) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, it)
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
            retriever.release()

            if (durationMs != null && durationMs <= 30_000L && durationMs >= 3_000L) {
                onAudioPicked(it)
            } else {
                val message =
                    if (durationMs == null || durationMs <= 3_000L) "Audio to short (min 3 seconds) "
                    else "Audio too long (max 30 seconds)"
                onAudioPicked(Uri.EMPTY)
                showToastMessage(context, message)
            }
        }
    }
    SubmitButton(
        onClick = { launcher.launch("audio/*") },
        enabled = true, getUIStyle = getUIStyle, string = "Choose Audio",
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

fun Uri.toBitmap(context: Context): Bitmap? {
    try {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        return ImageDecoder.decodeBitmap(source)
    } catch (e: Exception) {
        Log.e("Uri.toBitmap()", " $e")
        return null
    }
}


fun copyFileToInternalStorage(
    context: Context, sourceUri: Uri,
    prefix: String, extName: String,
    maxWidth: Int = 1280, maxHeight: Int = 720, quality: Int = 85,
): Uri {


    val fileName = "${prefix}_${System.currentTimeMillis()}-${UUID.randomUUID()}.$extName"
    // 1. Open input and output streams
    val inputStream = context.contentResolver.openInputStream(sourceUri)
        ?: throw IllegalArgumentException("Cannot open input stream")
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
        ?: throw IllegalArgumentException("Cannot decode bitmap")

    // Resize while keeping aspect ratio
    val resizedBitmap = resizeBitmapMaintainingAspectRatio(originalBitmap, maxWidth, maxHeight)

    val outFile = File(context.filesDir, fileName)
    FileOutputStream(outFile).use { outputStream ->
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    }

    // 3. Return a file:// URI pointing at your private copy
    return Uri.fromFile(outFile)
}


private fun resizeBitmapMaintainingAspectRatio(
    bitmap: Bitmap,
    maxWidth: Int,
    maxHeight: Int
): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val aspectRatio = width.toFloat() / height
    val targetWidth: Int
    val targetHeight: Int

    if (width > height) {
        targetWidth = maxWidth
        targetHeight = (maxWidth / aspectRatio).toInt()
    } else {
        targetHeight = maxHeight
        targetWidth = (maxHeight * aspectRatio).toInt()
    }

    return bitmap.scale(targetWidth, targetHeight)
}

@Composable
fun AgainText(getUIStyle: GetUIStyle) {
    Text(
        "-----",
        color = getUIStyle.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )
}

@Composable
fun HardText(ct:CT, hard: Int, getUIStyle: GetUIStyle) {
    Text(
        text =
            if (returnReviewsLeft(ct) == 1) {
                "$hard " + stringResource(R.string.days)
            } else {
                "${returnReviewsLeft(ct)} " + "reviews left"
            },
        color = getUIStyle.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )

}

@Composable
fun GoodText(ct: CT, good: Int, getUIStyle: GetUIStyle) {
    Text(
        text =
            if (returnReviewsLeft(ct) == 1) {
                "$good " + stringResource(R.string.days)
            } else {
                "${
                    returnReviewsLeft(ct) - 1
                } " + "reviews left"
            },
        color = getUIStyle.titleColor(),
        fontSize = 12.sp,
        lineHeight = 14.sp
    )
}

@Composable
fun NoDueCards(getUIStyle: GetUIStyle) {
    var delay by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(400)
        delay = true
    }
    if (delay) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                stringResource(R.string.no_due_cards),
                fontSize = 25.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                color = getUIStyle.titleColor(),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}


@Composable
fun PickAnswerChar(fields: CDetails, getUIStyle: GetUIStyle, onUpdate: (Char) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = stringResource(R.string.answer) +
                            ": ${fields.correct.uppercase()}",
                    modifier = Modifier.padding(2.dp)
                )
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Answer",
                    tint = getUIStyle.titleColor(),
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
        Box(
            Modifier.fillMaxWidth(.25f),
            contentAlignment = Alignment.BottomEnd
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onUpdate('a')
                        expanded = false
                    },
                    text = { Text("A") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                DropdownMenuItem(
                    onClick = {
                        onUpdate('b')
                        expanded = false
                    },
                    text = { Text("B") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                if (fields.choices[2].isNotBlank()) {
                    DropdownMenuItem(
                        onClick = {
                            onUpdate('c')
                            expanded = false
                        },
                        text = { Text("C") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
                if (fields.choices[3].isNotBlank()) {
                    DropdownMenuItem(
                        onClick = {
                            onUpdate('d')
                            expanded = false
                        },
                        text = { Text("D") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun PickAnswerChar(
    choiceParam: MiddleParam.Choice, getUIStyle: GetUIStyle, onUpdate: (Char) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Row {
                Text(
                    text = stringResource(R.string.answer) + ": ${choiceParam.correct}",
                    modifier = Modifier.padding(2.dp)
                )
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Answer",
                    tint = getUIStyle.titleColor(),
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
        Box(
            Modifier.fillMaxWidth(.25f),
            contentAlignment = Alignment.BottomEnd
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                choiceParam.choices.forEachIndexed { index, _ ->
                    DropdownMenuItem(
                        onClick = {
                            onUpdate(index.toString()[0])
                            expanded = false
                        },
                        text = { Text("${index.toString()[0]}") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun returnReviewError(): List<String> {
    return listOf(
        stringResource(R.string.review_amount_0),
        stringResource(R.string.review_amount_10),
        stringResource(R.string.review_amount_same),
        stringResource(R.string.failed_review)
    )
}

@Composable
fun returnMultiplierError(): List<String> {
    return listOf(
        stringResource(R.string.good_multiplier_1),
        stringResource(R.string.bad_multiplier_1),
        stringResource(R.string.multipliers_same),
        stringResource(R.string.failed_multiplier)
    )
}

@Composable
fun returnDeckError(): List<String> {
    return listOf(
        stringResource(R.string.empty_deck_name),
        stringResource(R.string.deck_name_exists),
        stringResource(R.string.deck_name_failed)
    )
}

@Composable
fun returnCardAmountError(): List<String> {
    return listOf(
        stringResource(R.string.card_amount_under_5),
        stringResource(R.string.card_amount_over_1k),
        stringResource(R.string.card_amount_same),
        stringResource(R.string.failed_card_amount)
    )
}

@Composable
fun collectNotationFieldsAsStates(
    vm: AddCardViewModel
): Triple<CDetails, Boolean, SelectedKeyboard?> {
    val fields by vm.fields.collectAsStateWithLifecycle()
    val showKB by vm.showKatexKeyboard.collectAsStateWithLifecycle()
    val selectedKB by vm.selectedKB.collectAsStateWithLifecycle()
    return Triple(fields, showKB, selectedKB)
}

@Composable
fun collectNotationFieldsAsStates(
    vm: EditCardViewModel
): Triple<CDetails, Boolean, SelectedKeyboard?> {
    val fields by vm.fields.collectAsStateWithLifecycle()
    val showKB by vm.showKatexKeyboard.collectAsStateWithLifecycle()
    val selectedKB by vm.selectedKB.collectAsStateWithLifecycle()
    return Triple(fields, showKB, selectedKB)
}

const val CARD_CRAFTER = "CardCrafter"
