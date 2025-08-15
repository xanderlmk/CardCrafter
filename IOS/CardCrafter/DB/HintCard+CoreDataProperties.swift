//
//  HintCard+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 8/2/25.
//
//

import Foundation
import CoreData


extension HintCard {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<HintCard> {
        return NSFetchRequest<HintCard>(entityName: "HintCard")
    }

    @NSManaged public var answer: String
    @NSManaged public var hint: String
    @NSManaged public var question: String

}
