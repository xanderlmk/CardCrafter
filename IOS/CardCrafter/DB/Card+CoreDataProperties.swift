//
//  Card+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 7/30/25.
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

}

extension Card : Identifiable {

}
