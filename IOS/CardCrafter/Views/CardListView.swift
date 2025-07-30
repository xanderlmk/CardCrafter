//
//  CardDeckView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/28/25.
//

import SwiftUI
import CoreData

struct CardListView: View {
    @ObservedObject var deck : Deck

    var body: some View {
        let cards = (deck.cards as? Set<Card>)?.sorted { $0.c_createdOn < $1.c_createdOn } ?? []
        VStack {
            List {
                ForEach(cards, id: \.self) { card in
                    if let basic = card as? BasicCard {
                        Text("Basic: \(basic.question)")
                    } else if let three = card as? ThreeFieldCard {
                        Text("Three-Field: \(three.question)")
                    } else if let hint = card as? HintCard {
                        Text("Hint: \(hint.hint)")
                    } else {
                        Text("Unknown card type")
                    }
                }
            }
        }
    }
}
