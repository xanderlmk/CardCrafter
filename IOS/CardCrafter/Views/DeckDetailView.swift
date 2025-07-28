//
//  DeckDetailView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/27/25.
//


import SwiftUI
import CoreData

struct DeckDetailView: View {
    @ObservedObject var deck: Deck

    var body: some View {
        VStack(spacing: 20) {
            Text(deck.d_name)
                .font(.largeTitle)
                .bold()

            HStack {
                Text("Cards per day: \(deck.cardAmount)")
            }

            HStack {
                Text("Review amount: \(deck.reviewAmount)")
            }
            NavigationLink(destination: AddCardView(deck: deck)) {
                Text("Add New Card")
            }
        }
        .padding()
       // .navigationTitle("Deck Details")
    }
}
