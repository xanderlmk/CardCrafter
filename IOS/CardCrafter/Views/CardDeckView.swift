//
//  CardDeckView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/31/25.
//

import SwiftUI
import CoreData
struct CardDeckView : View {
    @ObservedObject var deck : Deck
    @Binding var index: Int
    @Environment(\.managedObjectContext) private var viewContext
    /** Sorted by the most recent on top (first) */
    @FetchRequest(
        entity: SavedCard.entity(),
        sortDescriptors: [NSSortDescriptor(key: "s_createdOn", ascending: false)]
    )
    private var savedCards: FetchedResults<SavedCard>
    @State private var showError = false
    @State private var enabled = true
    
    var body: some View {
        var canRedoPrevious: Bool {
            // 1) must be enabled
            guard enabled else { return false }
            // 2) must have a “previous” index in bounds
            guard index > 0, deck.dueCards.indices.contains(index - 1) else { return false }
            // 3) and that previous card must have at least one savedRecord
            return !deck.dueCards[index - 1].savedRecords.isEmpty
        }
        VStack {
            if deck.dueCards.isEmpty {
                Text("No due cards")
                    .foregroundColor(.secondary)
                    .italic()
            } else {
                let currentCard = deck.dueCards[index]
                if let basic = currentCard as? BasicCard {
                    Text("\(basic.question)")
                } else if let three = currentCard as? ThreeFieldCard {
                    Text("\(three.question)")
                } else if let hint = currentCard as? HintCard {
                    Text("\(hint.question)")
                } else if let multi = currentCard as? MultiChoiceCard {
                    Text("\(multi.question)")
                } else {
                    Text("Error: Unknown Card Type Stored")
                }
                let good = Int(Double(deck.dueCards[index].passes + 1) * deck.goodMultiplier)
                let hard = if (deck.dueCards[index].passes > 0){
                    Int(Double(deck.dueCards[index].passes + 1) * deck.badMultiplier)
                } else {
                    Int(Double(deck.dueCards[index].passes) * deck.badMultiplier)
                }
                HStack {
                    VStack {
                        Button(action: {
                            enabled = false
                            let result = updateCard(isSuccess: false, again: false)
                            if(!result) { showError = true }
                            enabled = true
                        }) {
                            Text("Again")
                        }.disabled(!enabled)
                        Text("---")
                    }
                    VStack {
                        Button(action: {
                            enabled = false
                            let result = updateCard(isSuccess: false, again: false)
                            if(result){
                                let newIndex = index + 1
                                if deck.dueCards.indices.contains(newIndex) {
                                    index = newIndex
                                } else {
                                    index = 0
                                }
                            } else { showError = true }
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
                    VStack{
                        Button(action: {
                            enabled = false
                            let result = updateCard(isSuccess: true, again: false)
                            if(result){
                                let newIndex = index + 1
                                if deck.dueCards.indices.contains(newIndex) {
                                    index = newIndex
                                } else {
                                    index = 0
                                }
                            } else { showError = true }
                            enabled = true
                        }) {
                            Text("Good")
                        }.disabled(!enabled)
                        if currentCard.reviewsLeft == 1 {
                            Text("\(good) days")
                        } else {
                            Text("\(currentCard.reviewsLeft - 1) reviews left")
                        }
                    }
                    
                }.alert("Failed to update card", isPresented: $showError) {
                    Button("OK", role: .cancel) { }
                }
            }
        }.toolbar {
#if os(iOS)
            ToolbarItem(placement: .navigationBarTrailing) {
                Button{
                    enabled = false
                    let result = redoCard()
                    if(result){ index -= 1  } else { showError = true }
                    enabled = true
                } label: {
                    Label("Redo", systemImage: "arrowshape.turn.up.backward.fill")
                }.disabled(!canRedoPrevious)
            }
#else
            ToolbarItem {
                Button{
                    enabled = false
                    let result = redoCard()
                    if(result){ index -= 1 } else { showError = true }
                    enabled = true
                } label: {
                    Label("Redo", systemImage: "arrowshape.turn.up.backward.fill")
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
            
            if(temp.reviewsLeft <= 1) {
                if(isSuccess) {
                    temp.passes += 1
                    temp.prevSuccess = true
                    temp.reviewsLeft = deck.reviewAmount
                } else {
                    if (!again) {
                        temp.reviewsLeft = deck.reviewAmount
                    }
                    temp.prevSuccess = false
                }
                temp.c_nextReview = timeCalculator(passes: Int(temp.passes), isSuccess: isSuccess)
                if (!isSuccess && !prevSuccess && temp.passes > 0) {
                    temp.passes -= 1
                }
            } else {
                /** When the user reviews a card x amount of times
                 *  Default value is 1
                 */
                if (isSuccess) {
                    temp.reviewsLeft -= 1
                }
            }
            temp.totalPasses += 1
            temp.partOfList = true
            let saved = SavedCard(context: viewContext)
            saved.s_createdOn = Date()
            saved.s_totalPasses = temp.totalPasses
            saved.s_passes = temp.passes
            saved.s_partOfList = temp.partOfList
            saved.s_nextReview = temp.c_nextReview
            saved.s_prevSuccess = temp.prevSuccess
            saved.s_reviewsLeft = temp.reviewsLeft
            saved.details = temp
            try viewContext.save()
            return true
        } catch {
            viewContext.rollback()
            return false
        }
    }
    
    private func timeCalculator(passes: Int, isSuccess: Bool) -> Date {
        let now = Date()
        let multiplier = calculateReviewMultiplier(passes: passes,isSuccess: isSuccess)
        let daysToAdd = Int(Double(passes) * multiplier)
        return Calendar.current.date(
            byAdding: .day,
            value: daysToAdd,
            to: now
        )!
    }
    
    private func calculateReviewMultiplier(passes: Int,isSuccess: Bool) -> Double {
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
