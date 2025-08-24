//
//  CardDeckView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/31/25.
//

import CoreData
import SwiftUI

struct CardDeckView: View {
    @ObservedObject var deck: Deck
    @Binding var index: Int
    @Environment(\.managedObjectContext) private var viewContext
    /** Sorted by the most recent on top (first) */
    @FetchRequest(
        entity: SavedCard.entity(),
        sortDescriptors: [
            NSSortDescriptor(key: "s_createdOn", ascending: false)
        ]
    )
    private var savedCards: FetchedResults<SavedCard>
    @State private var showError = false
    @State private var enabled = true
    @State private var showBack = false
    @State var pickedChoice: Character = "?"

    var body: some View {
        var canRedoPrevious: Bool {
            // 1) must be enabled
            guard enabled else { return false }
            // 2) Must have at least one savedRecord
            return !savedCards.isEmpty
        }
        VStack {
            if deck.dueCards.isEmpty {
                Text("No due cards")
                    .foregroundColor(.secondary)
                    .italic()
            } else {
                let currentCard = deck.dueCards[index]
                Group {
                    Text("Review Left: \(currentCard.reviewsLeft)")
                        .padding(.top, 5)
                    if !showBack {
                        FrontCard(
                            card: currentCard,
                            pickedChoice: $pickedChoice
                        )
                        .padding(.top, 10)
                    } else {
                        BackCard(card: currentCard, pickedChoice: $pickedChoice)
                            .padding(.top, 10)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .top)

                Spacer()
                if !showBack {
                    Button(action: {
                        showBack = true
                    }) {
                        Text("Show")
                    }.disabled(!enabled)
                } else {
                    let good = Int(
                        Double(deck.dueCards[index].passes + 1)
                            * deck.goodMultiplier
                    )
                    let hard =
                        deck.dueCards[index].passes > 0
                        ? Int(
                            Double(deck.dueCards[index].passes + 1)
                                * deck.badMultiplier
                        )
                        : Int(
                            Double(deck.dueCards[index].passes)
                                * deck.badMultiplier
                        )
                    HStack(
                        spacing: {
                            #if os(macOS)
                                40
                            #else
                                5
                            #endif
                        }()
                    ) {
                        VStack(alignment: .center) {
                            Button(action: {
                                enabled = false
                                let result = updateCard(
                                    isSuccess: false,
                                    again: false
                                )
                                if !result { showError = true }
                                showBack = false
                                enabled = true
                            }) {
                                Text("Again")
                            }.disabled(!enabled)
                            Text("---")
                        }
                        VStack(alignment: .center) {
                            Button(action: {
                                enabled = false
                                let beforeSize = deck.dueCards.count
                                let result = updateCard(
                                    isSuccess: false,
                                    again: false
                                )
                                if result {
                                    let afterSize = deck.dueCards.count
                                    let newIndex = index + 1
                                    if deck.dueCards.isNotEmpty
                                        && afterSize == beforeSize
                                    {
                                        if deck.dueCards.indices.contains(
                                            newIndex
                                        ) {
                                            index = newIndex
                                        } else {
                                            index = 0
                                        }
                                    }
                                } else {
                                    showError = true
                                }
                                showBack = false
                                enabled = true
                            }) {
                                Text("Hard")
                            }.disabled(!enabled)

                            if currentCard.reviewsLeft == 1 {
                                Text("\(hard) days")
                            } else {
                                Text("\(currentCard.reviewsLeft) reviews left")
                            }
                        }
                        VStack(alignment: .center) {
                            Button(action: {
                                enabled = false
                                let beforeSize = deck.dueCards.count
                                let result = updateCard(
                                    isSuccess: true,
                                    again: false
                                )
                                if result {
                                    let afterSize = deck.dueCards.count
                                    let newIndex = index + 1
                                    if deck.dueCards.isNotEmpty
                                        && afterSize == beforeSize
                                    {
                                        if deck.dueCards.indices.contains(
                                            newIndex
                                        ) {
                                            index = newIndex
                                        } else {
                                            index = 0
                                        }
                                    }
                                } else {
                                    showError = true
                                }
                                showBack = false
                                enabled = true
                            }) {
                                Text("Good")
                            }.disabled(!enabled)
                            if currentCard.reviewsLeft == 1 {
                                Text("\(good) days")
                            } else {
                                Text(
                                    "\(currentCard.reviewsLeft - 1) reviews left"
                                )
                            }
                        }
                    }
                    .padding(.bottom, 16)
                    .frame(maxWidth: .infinity)

                }
            }
        }
        .frame(maxHeight: .infinity)
        .padding()
        .alert("Failed to update card", isPresented: $showError) {
            Button("OK", role: .cancel) {}
        }.toolbar {
            #if os(iOS)
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        enabled = false
                        let beforeSize = deck.dueCards.count
                        let result = redoCard()
                        if result {
                            let afterSize = deck.dueCards.count
                            if deck.dueCards.isNotEmpty
                                && afterSize == beforeSize
                            {
                                if index > 0 {
                                    index -= 1
                                } else {
                                    index = deck.dueCards.count - 1
                                }
                            }
                        } else {
                            showError = true
                        }
                        showBack = false
                        enabled = true
                    } label: {
                        Label(
                            "Redo",
                            systemImage: "arrowshape.turn.up.backward.fill"
                        )
                    }.disabled(!canRedoPrevious)
                }
            #else
                ToolbarItem {
                    Button {
                        enabled = false
                        let beforeSize = deck.dueCards.count
                        let result = redoCard()
                        if result {
                            let afterSize = deck.dueCards.count
                            if deck.dueCards.isNotEmpty
                                && afterSize == beforeSize
                            {
                                if index > 0 {
                                    index -= 1
                                } else {
                                    index = deck.dueCards.count - 1
                                }
                            }
                        } else {
                            showError = true
                        }
                        showBack = false
                        enabled = true
                    } label: {
                        Label(
                            "Redo",
                            systemImage: "arrowshape.turn.up.backward.fill"
                        )
                    }.disabled(!canRedoPrevious)
                }
            #endif
        }
    }

    private func updateCard(isSuccess: Bool, again: Bool) -> Bool {
        do {
            guard deck.dueCards.indices.contains(index) else { return false }
            let temp = deck.dueCards[index]
            let prevSuccess = temp.prevSuccess
            let saved = SavedCard(context: viewContext)
            saved.s_createdOn = Date()
            saved.s_totalPasses = temp.totalPasses
            saved.s_passes = temp.passes
            saved.s_partOfList = temp.partOfList
            saved.s_nextReview = temp.c_nextReview
            saved.s_prevSuccess = temp.prevSuccess
            saved.s_reviewsLeft = temp.reviewsLeft
            saved.details = temp
            if temp.reviewsLeft <= 1 {
                if isSuccess {
                    temp.passes += 1
                    temp.prevSuccess = true
                    temp.reviewsLeft = deck.reviewAmount
                } else {
                    if !again {
                        temp.reviewsLeft = deck.reviewAmount
                    }
                    temp.prevSuccess = false
                }
                temp.c_nextReview = timeCalculator(
                    passes: Int(temp.passes),
                    isSuccess: isSuccess
                )
                if !isSuccess && !prevSuccess && temp.passes > 0 {
                    temp.passes -= 1
                }
            } else {
                /** When the user reviews a card x amount of times
                 *  Default value is 1
                 */
                if isSuccess {
                    temp.reviewsLeft -= 1
                }
            }
            temp.totalPasses += 1
            temp.partOfList = true
            try viewContext.save()
            return true
        } catch {
            viewContext.rollback()
            return false
        }
    }

    private func timeCalculator(passes: Int, isSuccess: Bool) -> Date {
        let now = Date()
        let multiplier = calculateReviewMultiplier(
            passes: passes,
            isSuccess: isSuccess
        )
        let daysToAdd = Int(Double(passes) * multiplier)
        return Calendar.current.date(
            byAdding: .day,
            value: daysToAdd,
            to: now
        )!
    }

    private func calculateReviewMultiplier(passes: Int, isSuccess: Bool)
        -> Double
    {
        let baseMultiplier: Double
        switch passes {
        case 1:
            baseMultiplier = 1.0
        case 2...:
            baseMultiplier = deck.badMultiplier
        default:
            baseMultiplier = 0.0
        }
        return isSuccess ? deck.goodMultiplier : baseMultiplier
    }
    private func redoCard() -> Bool {
        do {
            guard !savedCards.isEmpty else { return false }
            guard let record = savedCards.first else { return false }
            /** Where record.details is just Card */
            let temp = record.details
            temp.c_nextReview = record.s_nextReview
            temp.reviewsLeft = record.s_reviewsLeft
            temp.passes = record.s_passes
            temp.totalPasses = record.s_totalPasses
            temp.prevSuccess = record.s_prevSuccess
            temp.partOfList = record.s_partOfList
            /** Delete the record, pushing it out of the list so you can redo again to the previous (if there is one)*/
            viewContext.delete(record)
            try viewContext.save()
            return true
        } catch {
            viewContext.rollback()
            return false
        }
    }
}
