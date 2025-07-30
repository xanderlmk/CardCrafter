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
                ToolbarItem(placement: .navigationBarTrailing) {
                    EditButton()
                }
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
        }
    }
    
    private func deleteDecks(offsets: IndexSet) {
        offsets.map { decks[$0] }.forEach(viewContext.delete)
        try? viewContext.save()
    }
}


#Preview {
    ContentView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
}
