//
//  ThreeFieldCard+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 8/2/25.
//
//

import Foundation
import CoreData


extension ThreeFieldCard {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<ThreeFieldCard> {
        return NSFetchRequest<ThreeFieldCard>(entityName: "ThreeFieldCard")
    }

    @NSManaged public var answer: String
    @NSManaged public var middle: String
    @NSManaged public var partOfQorA: Bool
    @NSManaged public var question: String

}
