//
//  Card+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 8/2/25.
//
//

import Foundation
import CoreData


extension Card {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Card> {
        return NSFetchRequest<Card>(entityName: "Card")
    }

    @NSManaged public var c_createdOn: Date
    @NSManaged public var c_nextReview: Date
    @NSManaged public var cardDeckNumber: Int32
    @NSManaged public var cardIdentifier: String
    @NSManaged public var partOfList: Bool
    @NSManaged public var passes: Int32
    @NSManaged public var prevSuccess: Bool
    @NSManaged public var reviewsLeft: Int16
    @NSManaged public var totalPasses: Int32
    @NSManaged public var type: String
    @NSManaged public var deck: Deck
    @NSManaged public var saved: NSSet

}

extension Card : Identifiable {
    var savedRecords: [SavedCard] {
        (saved as? Set<SavedCard> ?? []).sorted { $0.s_createdOn > $1.s_createdOn }
    }
}
