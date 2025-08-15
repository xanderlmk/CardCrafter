//
//  MultiChoiceCard+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 8/2/25.
//
//

import Foundation
import CoreData


extension MultiChoiceCard {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<MultiChoiceCard> {
        return NSFetchRequest<MultiChoiceCard>(entityName: "MultiChoiceCard")
    }

    @NSManaged public var choiceA: String
    @NSManaged public var choiceB: String
    @NSManaged public var choiceC: String?
    @NSManaged public var choiceD: String?
    @NSManaged public var correct: String
    @NSManaged public var question: String

}
