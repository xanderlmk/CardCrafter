package com.belmontCrest.cardCrafter.views.miscFunctions

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.belmontCrest.cardCrafter.R
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard
import com.belmontCrest.cardCrafter.model.uiModels.SealedAllCTs

@Composable
fun BasicCardQuestion(basicCard: BasicCard) {
    Text(
        text = stringResource(R.string.question) + ": ${basicCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Ellipsis

    )
}

@Composable
fun ThreeCardQuestion(threeFieldCard: ThreeFieldCard) {
    Text(
        text = stringResource(R.string.question) + ": ${threeFieldCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis

    )
}

@Composable
fun HintCardQuestion(hintCard: HintCard) {
    Text(
        text = stringResource(R.string.question) + ": ${hintCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis

    )
}

@Composable
fun ChoiceCardQuestion(multiChoiceCard: MultiChoiceCard) {
    Text(
        text = stringResource(R.string.question) + ": ${multiChoiceCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis
    )
}

@Composable
fun NotationCardQuestion(notationCard: NotationCard){
    Text(
        text = stringResource(R.string.question) + ": ${notationCard.question}",
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis
    )
}

@Composable
fun CardSelector(
    sealedAllCTs: SealedAllCTs,
    index: Int
) {
    val ct = sealedAllCTs.allCTs[index]
    when(ct){
        is CT.Basic -> {
            BasicCardQuestion(ct.basicCard)
        }
        is CT.ThreeField -> {
            ThreeCardQuestion(ct.threeFieldCard)
        }
        is CT.Hint -> {
            HintCardQuestion(ct.hintCard)
        }
        is CT.MultiChoice -> {
            ChoiceCardQuestion(ct.multiChoiceCard)
        }
        is CT.Notation -> {
            NotationCardQuestion(ct.notationCard)
        }
    }
}
