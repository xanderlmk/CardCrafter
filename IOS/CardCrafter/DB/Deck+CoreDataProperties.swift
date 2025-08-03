//
//  Deck+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 8/2/25.
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
    var dueCards: [Card] {
           Array(
               (cards as? Set<Card> ?? [])
                   .filter { $0.c_nextReview <= Date() }
                   .sorted { (a: Card, b: Card) -> Bool in
                       if a.c_nextReview != b.c_nextReview { return a.c_nextReview < b.c_nextReview }
                       if a.partOfList   != b.partOfList   { return a.partOfList && !b.partOfList }
                       return a.reviewsLeft > b.reviewsLeft
                   }
                   .prefix(Int(cardsLeft))
           )
       }
}
