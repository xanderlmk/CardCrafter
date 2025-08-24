//
//  FrontCard.swift
//  CardCrafter
//
//  Created by Assykilla on 8/10/25.
//

import CoreData
import SwiftUI

struct FrontCard: View {
    @ObservedObject var card: Card
    @Binding var pickedChoice: Character

    var body: some View {
        VStack {
            if let basic = card as? BasicCard {
                Text("\(basic.question)")
            } else if let three = card as? ThreeFieldCard {
                Text("\(three.question)")
                if three.partOfQorA {
                    Text("\(three.middle)")
                }
            } else if let hint = card as? HintCard {
                @State var showHint = false
                Text("\(hint.question)")
                Text(showHint ? hint.hint : "Hint")
                    .onTapGesture { showHint.toggle() }
                    .foregroundColor(.secondary)

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
                        FrontChoiceView(
                            choice: option.text,
                            clickedChoice: pickedChoice,
                            letter: Character(option.key)
                        ) {
                            pickedChoice = Character(option.key)
                        }
                    }
                }

            } else {
                Text("Error: Unknown Card Type Stored")
            }
        }
    }
}
