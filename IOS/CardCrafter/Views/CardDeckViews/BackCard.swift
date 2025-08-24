//
//  BackCard.swift
//  CardCrafter
//
//  Created by Assykilla on 8/10/25.
//
import SwiftUI
import CoreData

struct BackCard : View {
    @ObservedObject var card: Card
    @Binding var pickedChoice: Character

    var body: some View {
        VStack {
            if let basic = card as? BasicCard {
                Text("\(basic.question)")
                Text("\(basic.answer)")
            } else if let three = card as? ThreeFieldCard {
                Text("\(three.question)")
                if !three.partOfQorA {
                    Text("\(three.middle)")
                }
                Text("\(three.answer)")

            } else if let hint = card as? HintCard {
                @State var showHint = false
                Text("\(hint.question)")
                Text("\(hint.answer)")
            } else if let multi = card as? MultiChoiceCard {
                Text("\(multi.question)")
                    .font(.headline)
                let options: [(text: String, key: String)] = [
                    (multi.choiceA, "a"),
                    (multi.choiceB, "b"),
                    (multi.choiceC ?? "", "c"),
                    (multi.choiceD ?? "", "d"),
                ].filter {
                    !$0.text.trimmingCharacters(in: .whitespacesAndNewlines)
                        .isEmpty
                }

                VStack(spacing: 8) {
                    ForEach(Array(options), id: \.key) { option in
                        BackChoiceView(
                            choice: option.text, correctChoice: Character(multi.correct),
                            clickedChoice: pickedChoice, letter: Character(option.key)
                        )
                    }
                }
                            } else {
                Text("Error: Unknown Card Type Stored")
            }
        }
    }
}
