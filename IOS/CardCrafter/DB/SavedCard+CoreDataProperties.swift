//
//  SavedCard+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 8/2/25.
//
//

import Foundation
import CoreData


extension SavedCard {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<SavedCard> {
        return NSFetchRequest<SavedCard>(entityName: "SavedCard")
    }

    @NSManaged public var s_reviewsLeft: Int16
    @NSManaged public var s_nextReview: Date
    @NSManaged public var s_passes: Int32
    @NSManaged public var s_totalPasses: Int32
    @NSManaged public var s_prevSuccess: Bool
    @NSManaged public var s_partOfList: Bool
    @NSManaged public var s_createdOn: Date
    @NSManaged public var details: Card

}

extension SavedCard : Identifiable {

}
