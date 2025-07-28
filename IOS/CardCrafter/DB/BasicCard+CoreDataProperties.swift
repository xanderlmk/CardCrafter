//
//  BasicCard+CoreDataProperties.swift
//  CardCrafter
//
//  Created by Assykilla on 7/28/25.
//
//

import Foundation
import CoreData


extension BasicCard {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<BasicCard> {
        return NSFetchRequest<BasicCard>(entityName: "BasicCard")
    }

    @NSManaged public var answer: String
    @NSManaged public var question: String

}
