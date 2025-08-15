//
//  CardCrafterApp.swift
//  CardCrafter
//
//  Created by Assykilla on 7/27/25.
//

import SwiftUI

@main
struct CardCrafterApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
