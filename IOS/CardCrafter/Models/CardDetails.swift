//
//  CardDetails.swift
//  CardCrafter
//
//  Created by Assykilla on 7/30/25.
//

import SwiftUI

struct CardDetails: Identifiable, Codable {
    var id = UUID()
    var question: String =  ""
    var middle: String = ""
    var answer: String = ""
    var partOfQOrA: Bool = false
    var choiceA: String = ""
    var choiceB: String = ""
    var choiceC: String = ""
    var choiceD: String = ""
    var correct: Character = "?"
}

extension CardDetails {
    func resetDetails() -> CardDetails {
        return CardDetails()
    }
}

extension Character: Codable {
  public init(from decoder: Decoder) throws {
    let container = try decoder.singleValueContainer()
    let s = try container.decode(String.self)
    guard s.count == 1, let ch = s.first else {
      throw DecodingError.dataCorruptedError(in: container,
        debugDescription: "Expected single-character string.")
    }
    self = ch
  }
  public func encode(to encoder: Encoder) throws {
    var container = encoder.singleValueContainer()
    try container.encode(String(self))
  }
}
