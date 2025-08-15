//
//  EditDeckView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/29/25.
//

import SwiftUI
import CoreData

struct EditDeckView: View {
    @ObservedObject var deck: Deck
    @State private var deckName: String
    @State private var reviewAmount: Int16
    @State private var cardAmount: Int16
    @State private var goodMultiplier: Double
    @State private var badMultiplier: Double
    @State private var errorMessage: String?
    @Environment(\.managedObjectContext) private var viewContext
    @Environment(\.presentationMode) private var presentationMode
    
    init(deck: Deck) {
        self.deck = deck
        _deckName = State(initialValue: deck.d_name)
        _reviewAmount = State(initialValue: deck.reviewAmount)
        _cardAmount = State(initialValue: deck.cardAmount)
        _goodMultiplier = State(initialValue: deck.goodMultiplier)
        _badMultiplier = State(initialValue: deck.badMultiplier)
    }
    
    var body: some View {
        VStack {
            Form {
                Section(header: Text("Deck name")) {
                    TextField("Name", text: $deckName)
                }
                Section(header: Text("Cards per day")) {
                    TextField("Card Amount", value: $cardAmount, formatter: Self.intFormatter)
#if os(iOS)
                        .keyboardType(.numberPad)
#endif
                    
                }
                Section(header: Text("Review Amount")) {
                    TextField("Review amount",value: $reviewAmount, formatter: Self.intFormatter)
#if os(iOS)
                        .keyboardType(.numberPad)
#endif
                    
                }
                Section {
                    Button("Update") {
                        updateDeck()
                    }
                    // disable if name empty or numbers invalid
                    .disabled(
                        deckName.trimmingCharacters(in: .whitespaces).isEmpty ||
                        cardAmount <= 0 ||
                        reviewAmount <= 0
                    )
                    
                    if let error = errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                }
            }
        }
        .navigationTitle("\(deck.d_name)")
#if os(macOS)
        .padding()
#endif
    }
    private static let intFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .none
        return formatter
    }()
    private func updateDeck() {
        let trimmedName = deckName.trimmingCharacters(in: .whitespaces)
        
        guard !trimmedName.isEmpty else {
            errorMessage = "Deck name can’t be blank."
            return
        }
        guard (5...1000).contains(cardAmount) else {
            errorMessage = "Cards per day must be between 5 and 1000."
            return
        }
        guard(1...40).contains(reviewAmount) else {
            errorMessage = "Review amount must be between 1 and 40"
            return
        }
        
        let fetch: NSFetchRequest<Deck> = Deck.fetchRequest()
        fetch.predicate = NSPredicate(
            format: "d_name == %@ AND SELF != %@",
            trimmedName,deck
        )
        fetch.fetchLimit = 1
        
        do {
            let matches = try viewContext.fetch(fetch)
            if !matches.isEmpty {
                // Some other deck already has this name
                errorMessage = "A deck named “\(trimmedName)” already exists."
                return
            }
        } catch {
            errorMessage = "Validation error: \(error.localizedDescription)"
            return
        }
        
        do {
            deck.d_name = deckName.trimmingCharacters(in: .whitespaces)
            deck.cardAmount = cardAmount
            deck.reviewAmount = reviewAmount
            deck.goodMultiplier = goodMultiplier
            deck.badMultiplier = badMultiplier
            deck.lastUpdated = Date()
            try viewContext.save()
            presentationMode.wrappedValue.dismiss()
        } catch {
            errorMessage = "Failed to update deck: \(error.localizedDescription)"
        }
    }
}
