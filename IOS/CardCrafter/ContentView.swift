//
//  ContentView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/27/25.
//

import SwiftUI
import CoreData

struct ContentView: View {
    @Environment(\.managedObjectContext) private var viewContext

    
    @FetchRequest(sortDescriptors: []) private var decks: FetchedResults<Deck>

    @State private var showingAddDeck = false

        var body: some View {
            NavigationView {
                List {
                    ForEach(decks) { deck in
                        NavigationLink(deck.d_name) {
                            DeckDetailView(deck: deck)
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
                        Button(action: { showingAddDeck = true }) {
                            Label("Add Deck", systemImage: "plus")
                        }
                    }
                }
                .sheet(isPresented: $showingAddDeck) {
                    AddDeckView()
                        .environment(\.managedObjectContext, viewContext)
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
