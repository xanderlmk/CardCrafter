//
//  AddDeckView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/27/25.
//


import SwiftUI
import CoreData

struct AddDeckView: View {
    @Environment(\.managedObjectContext) private var viewContext
    @Environment(\.presentationMode) private var presentationMode
    
    @State private var name: String = ""
    @State private var cardAmount: Int = 20
    @State private var reviewAmount: Int = 1
    @State private var errorMessage: String?
    
    // A shared NumberFormatter for integer text fields
    private static let intFormatter: NumberFormatter = {
        let f = NumberFormatter()
        f.numberStyle = .none
        return f
    }()
    
    var body: some View {
        VStack {
            Form {
                Section(header: Text("Deck Info")) {
                    TextField("Name", text: $name)
                    TextField(
                        "Cards per day",
                        value: $cardAmount,
                        formatter: Self.intFormatter,
                    )
#if os(iOS)
                    .keyboardType(.numberPad)
#endif
                    TextField(
                        "Review amount",
                        value: $reviewAmount,
                        formatter: Self.intFormatter
                    )
#if os(iOS)
                    .keyboardType(.numberPad)
#endif
                }
                
                Section {
                    Button("Save") {
                        saveDeck()
                    }
                    // disable if name empty or numbers invalid
                    .disabled(
                        name.trimmingCharacters(in: .whitespaces).isEmpty ||
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
            .navigationTitle("New Deck")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
        }
#if os(macOS)
        .padding()
#endif
    }
    
    private func saveDeck() {
        // Generate a new UUID
        let newUUID = UUID().uuidString
        
        // Check for duplicates by name OR uuid
        let fetch: NSFetchRequest<Deck> = Deck.fetchRequest()
        fetch.predicate = NSPredicate(
            format: "d_name == %@ OR d_uuid == %@",
            name, newUUID
        )
        fetch.fetchLimit = 1
        
        do {
            let matches = try viewContext.fetch(fetch)
            if !matches.isEmpty {
                // Duplicate found
                errorMessage = matches[0].d_name == name
                ? "A deck named “\(name)” already exists."
                : "UUID collision—please try again."
                return
            }
        } catch {
            errorMessage = "Validation error: \(error.localizedDescription)"
            return
        }
        if (name.isBlank) {
            errorMessage = "Name can't be empty"
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
        
        // No duplicate, proceed to save
        let deck = Deck(context: viewContext)
        deck.d_name = name.trimmingCharacters(in: .whitespaces)
        deck.d_uuid = newUUID
        deck.cardAmount = Int16(cardAmount)
        deck.reviewAmount = Int16(reviewAmount)
        deck.d_createdOn = Date()
        deck.d_nextReview = Date()
        deck.lastUpdated = Date()
        deck.cardsDone = 0
        deck.cardsLeft = Int16(cardAmount)
        
        do {
            try viewContext.save()
            presentationMode.wrappedValue.dismiss()
            name = ""
            cardAmount = 20
            reviewAmount = 1
        } catch {
            errorMessage = "Save failed: \(error.localizedDescription)"
        }
    }
}
