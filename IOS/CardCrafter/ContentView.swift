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
                DeckDetailView(deck: deck, path: $path)
                case .editDeck(let deck):
                    EditDeckView(deck: deck)
                case .editCards(let deck):
                    CardListView(deck: deck)
                }
            }
            .onAppear {
                do {
                    try resetCardLefts()
                } catch {
                    print("resetCardLefts failed:", error)
                }
            }
        }
    }
    
    private func deleteDecks(offsets: IndexSet) {
        offsets.map { decks[$0] }.forEach(viewContext.delete)
        try? viewContext.save()
    }
    
    private func resetCardLefts(
        currentTime: Date = .init(),
        startOfDay: Date = Calendar.current.startOfDay(for: .init())
    ) throws {
        // 1. Fetch all Decks that need resetting
        let deckReq: NSFetchRequest<Deck> = Deck.fetchRequest()
        deckReq.predicate = NSCompoundPredicate(andPredicateWithSubpredicates: [
            NSPredicate(format: "d_nextReview <= %@", currentTime as NSDate),
            NSPredicate(format: "lastUpdated != %@", startOfDay as NSDate),
            // NOT EXISTS a card in this deck with partOfList==true AND lastUpdated==startOfDay
            NSPredicate(format: "NOT (ANY cards.partOfList == true AND lastUpdated == %@)", startOfDay as NSDate)
        ])
        let decks = try viewContext.fetch(deckReq)

        for deck in decks {
            // Count of due cards in this deck
            let dueCount = deck.cards
                .compactMap { $0 as? Card }
                .filter { $0.c_nextReview <= currentTime }
                .count
            // Get the most Recent SavedCard with the Card itself
            let recentByCard: [(card: Card, saved: SavedCard)] = (deck.cards as? Set<Card> ?? [])
                .compactMap { card in
                    guard let rec = card.savedRecords.first else { return nil }
                    return (card, rec)
                }
            // For each Pair, update the Card based on the most recent SavedCard
            for pair in recentByCard {
                let card = pair.card
                let saved = pair.saved
                card.c_nextReview = saved.s_nextReview
                card.reviewsLeft = saved.s_reviewsLeft
                card.passes = saved.s_passes
                card.totalPasses = saved.s_totalPasses
                card.partOfList = saved.s_nextReview <= Date()
                card.prevSuccess = saved.s_prevSuccess
            }
            // Get all SavedCards and then delete them.
            let savedCards: [SavedCard] = (deck.cards as? Set<Card> ?? [])
                .flatMap { $0.savedRecords }
            
            for card in savedCards { viewContext.delete(card) }
            
            // Clamp between 0 and deck.cardAmount
            let maxAllowed = Int(deck.cardAmount)
            deck.cardsLeft = Int16(min(dueCount, maxAllowed))
            deck.cardsDone = 0
            deck.lastUpdated = startOfDay
        }
        try viewContext.save()
    }
}


#Preview {
    ContentView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
}
