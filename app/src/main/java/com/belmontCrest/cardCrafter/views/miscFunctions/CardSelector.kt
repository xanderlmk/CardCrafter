package com.belmontCrest.cardCrafter.views.miscFunctions

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.belmontCrest.cardCrafter.localDatabase.tables.BasicCard
import com.belmontCrest.cardCrafter.localDatabase.tables.CT
import com.belmontCrest.cardCrafter.localDatabase.tables.HintCard
import com.belmontCrest.cardCrafter.localDatabase.tables.NotationCard
import com.belmontCrest.cardCrafter.localDatabase.tables.MultiChoiceCard
import com.belmontCrest.cardCrafter.localDatabase.tables.ThreeFieldCard

@Composable
fun BasicCardQuestion(basicCard: BasicCard) {
    Text(
        text = basicCard.question,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis

    )
}

@Composable
fun ThreeCardQuestion(threeFieldCard: ThreeFieldCard) {
    Text(
        text = threeFieldCard.question,
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis

    )
}

@Composable
fun HintCardQuestion(hintCard: HintCard) {
    Text(
        text = hintCard.question,
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis

    )
}

@Composable
fun ChoiceCardQuestion(multiChoiceCard: MultiChoiceCard) {
    Text(
        text = multiChoiceCard.question,
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis
    )
}

@Composable
fun NotationCardQuestion(notationCard: NotationCard){
    Text(
        text = notationCard.question,
        maxLines = 3,
        overflow = TextOverflow.Companion.Ellipsis
    )
}

@Composable
fun CardSelector(
    allCTs: List<CT>,
    index: Int
) {
    val ct = allCTs[index]
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
