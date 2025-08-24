//
//  ContentView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/27/25.
//

import SwiftUI
import CoreData

enum Route: Hashable {
    case deckDetail(Deck)
    case editDeck(Deck)
    case editCards(Deck)
}

struct ContentView: View {
    
    @Environment(\.managedObjectContext) private var viewContext
    @FetchRequest(sortDescriptors: []) private var decks: FetchedResults<Deck>
    
    @State private var showingAddDeck = false
    @State private var path: [Route] = []
    
    
    var body: some View {
        NavigationStack(path: $path) {
            List {
                ForEach(decks) { deck in
                    NavigationLink(value: Route.deckDetail(deck)) {
                        Text(deck.d_name)
                    }
                }
                .onDelete(perform: deleteDecks)
            }
            .navigationTitle("Decks")
            .toolbar {
#if os(iOS)
                ToolbarItem(placement: .navigationBarTrailing) {
                    EditButton()
                }
#endif
                ToolbarItem {
                    Button {
                        showingAddDeck = true
                    } label: {
                        Label("Add Deck", systemImage: "plus")
                    }
                }
            }
            .sheet(isPresented: $showingAddDeck) {
                AddDeckView()
                    .environment(\.managedObjectContext, viewContext)
            }
            .navigationDestination(for: Route.self) { route in
                switch route {
                case .deckDetail(let deck):
                DeckDetailView(deck: deck, path: $path, onDismiss: {resetCardLefts()})
                case .editDeck(let deck):
                    EditDeckView(deck: deck)
                case .editCards(let deck):
                    CardListView(deck: deck)
                }
            }
            .onAppear { resetCardLefts() }
            
        }
    }
    
    private func deleteDecks(offsets: IndexSet) {
        offsets.map { decks[$0] }.forEach(viewContext.delete)
        try? viewContext.save()
    }
    
    private func resetCardLefts(
        currentTime: Date = .init(),
        startOfDay: Date = Calendar.current.startOfDay(for: .init())
    ) {
        viewContext.perform {
            do {
                // 1. Fetch all Decks that need resetting
                let deckReq: NSFetchRequest<Deck> = Deck.fetchRequest()
                deckReq.predicate = NSCompoundPredicate(andPredicateWithSubpredicates: [
                    NSPredicate(format: "d_nextReview <= %@", currentTime as NSDate),
                    NSPredicate(format: "lastUpdated != %@", startOfDay as NSDate),
                    NSPredicate(format: "NOT (ANY cards.partOfList == true)")
                ])
                let decks = try viewContext.fetch(deckReq)
                
                for deck in decks {
                    // Count of due cards in this deck
                    let dueCount = deck.cards
                        .compactMap { $0 as? Card }
                        .filter { $0.c_nextReview <= currentTime }
                        .count
                    
                    // Clamp between 0 and deck.cardAmount
                    let maxAllowed = Int(deck.cardAmount)
                    deck.cardsLeft = Int16(min(dueCount, maxAllowed))
                    deck.cardsDone = 0
                    deck.lastUpdated = startOfDay
                }
                // Delete all saved cards.
                let savedReq: NSFetchRequest<SavedCard> = SavedCard.fetchRequest()
                let savedCards = try viewContext.fetch(savedReq)
                print("SavedCard count BEFORE delete: \(savedCards.count)")
                for card in savedCards { viewContext.delete(card) }
                try viewContext.save()
                print("SavedCard count AFTER delete:", try self.viewContext.count(for: savedReq))

            } catch {
                print("resetCardLefts failed:", error)
                viewContext.rollback()
            }
        }
    }
}


#Preview {
    ContentView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
}
