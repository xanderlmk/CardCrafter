//
//  Deck+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 7/28/25.
//
//

import Foundation
import CoreData


extension Deck {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<Deck> {
        return NSFetchRequest<Deck>(entityName: "Deck")
    }

    @NSManaged public var badMultiplier: Double
    @NSManaged public var cardAmount: Int16
    @NSManaged public var cardsDone: Int16
    @NSManaged public var cardsLeft: Int16
    @NSManaged public var d_createdOn: Date
    @NSManaged public var d_name: String
    @NSManaged public var d_nextReview: Date
    @NSManaged public var d_uuid: String
    @NSManaged public var goodMultiplier: Double
    @NSManaged public var lastUpdated: Date
    @NSManaged public var reviewAmount: Int16
    @NSManaged public var deck_of: NSSet?

}

// MARK: Generated accessors for deck_of
extension Deck {

    @objc(addDeck_ofObject:)
    @NSManaged public func addToDeck_of(_ value: Card)

    @objc(removeDeck_ofObject:)
    @NSManaged public func removeFromDeck_of(_ value: Card)

    @objc(addDeck_of:)
    @NSManaged public func addToDeck_of(_ values: NSSet)

    @objc(removeDeck_of:)
    @NSManaged public func removeFromDeck_of(_ values: NSSet)

}

extension Deck : Identifiable {

}
