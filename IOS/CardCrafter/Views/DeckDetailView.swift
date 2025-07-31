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
    @Binding var path: [Route]
    
    var body: some View {
        VStack(spacing: 20) {
            Text(deck.d_name)
                .font(.largeTitle)
                .bold()
            Text("Cards per day: \(deck.cardAmount)")
            Text("Review amount: \(deck.reviewAmount)")
            NavigationLink(destination: AddCardView(deck: deck)) {
                Text("Add New Card")
            }
        }.toolbar {
#if os(iOS)
            ToolbarItem(placement: .navigationBarTrailing) {
                Menu {
                    Button("Edit Deck"){
                        path.append(Route.editDeck(deck))
                    }
                    Button("Edit Cards") {
                        path.append(Route.editCards(deck))
                    }
                } label: {
                    Label("Edit", systemImage: "pencil.circle.fill")
                }
                
            }
            
#else
            ToolbarItem() {
                Menu {
                    Button("Edit Deck"){
                        path.append(Route.editDeck(deck))
                    }
                    Button("Edit Cards") {
                        path.append(Route.editCards(deck))
                    }
                } label: {
                    Label("Edit", systemImage: "pencil.circle.fill")
                }
            }
#endif
        }
        .navigationTitle("\(deck.d_name)")
        .padding()
        
    }
}

