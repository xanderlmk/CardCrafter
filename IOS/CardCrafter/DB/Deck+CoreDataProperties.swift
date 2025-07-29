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
    @NSManaged public var cards: NSSet

}

// MARK: Generated accessors for cards
extension Deck {

    @objc(addCardsObject:)
    @NSManaged public func addToCards(_ value: Card)

    @objc(removeCardsObject:)
    @NSManaged public func removeFromCards(_ value: Card)

    @objc(addCards:)
    @NSManaged public func addToCards(_ values: NSSet)

    @objc(removeCards:)
    @NSManaged public func removeFromCards(_ values: NSSet)

}

extension Deck : Identifiable {

}
