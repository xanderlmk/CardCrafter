//
//  AddCardView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/28/25.
//
import SwiftUI
import CoreData

enum CardKind: String, CaseIterable, Identifiable {
    case basic, three, hint
    var id: String { rawValue }
}

struct AddCardView: View {
    @ObservedObject var deck: Deck

    @Environment(\.managedObjectContext) private var viewContext
    @Environment(\.presentationMode) private var presentationMode

    @State private var kind: CardKind = .basic
    @State private var question: String = ""
    @State private var middle: String = ""
    @State private var answer: String = ""
    @State private var partOfQOrA: Bool = false
    @State private var errorMessage: String?
    private var text: String {
        partOfQOrA ? "Question" : "Answer"
    }

    var body: some View {
        NavigationView {
            Form {
                Section("Type") {
                    Picker("Card Type", selection: $kind) {
                        ForEach(CardKind.allCases) { k in
                            Text(k.rawValue).tag(k)
                        }
                    }
                    .pickerStyle(SegmentedPickerStyle())
                }

                Section("Inputs") {
                    TextField("Question", text: $question)
                    if kind == .three || kind == .hint {
                        TextField("Middle", text: $middle)
                    }
                    if kind == .three {
                        Picker("Part of \(text)", selection: $partOfQOrA) {
                            Text("Question").tag(true)
                            Text("Answer").tag(false)
                        }
                        .pickerStyle(.segmented)
                    }
                    TextField("Answer", text: $answer)
                }

                Section {
                    Button("Save") {
                        addCard()
                    }
                    .disabled(
                        question.isEmpty ||
                        (kind == .three && middle.isEmpty) ||
                        (kind != .three && answer.isEmpty)
                    )

                    if let error = errorMessage {
                        Text(error).foregroundColor(.red)
                    }
                }
            }
            .navigationTitle("New Card")
        }
    }

    private func addCard() {
        // 1) Compute next cardDeckNumber & cardIdentifier
        let fetch: NSFetchRequest<Card> = Card.fetchRequest()
        fetch.predicate = NSPredicate(format: "card_of == %@", deck)
        fetch.sortDescriptors = [NSSortDescriptor(key: "cardDeckNumber", ascending: false)]
        fetch.fetchLimit = 1

        let nextNumber: Int32
        if let last = (try? viewContext.fetch(fetch))?.first {
            nextNumber = last.cardDeckNumber + 1
        } else {
            nextNumber = 1
        }
        let identifier = "\(deck.d_uuid)-\(nextNumber)"

        // 2) Instantiate the right subclass
        let card: Card
        switch kind.rawValue {
        case "basic":
            let c = BasicCard(context: viewContext)
            c.question = question
            c.answer = answer
            card = c

        case "three":
            let c = ThreeFieldCard(context: viewContext)
            c.question = question
            c.middle = middle
            c.answer = answer
            c.partOfQorA = partOfQOrA
            card = c

        case "hint":
            let c = HintCard(context: viewContext)
            c.question = question
            c.hint = middle
            c.answer = answer
            card = c

        default:
            fatalError("Unsupported card type: \(kind.rawValue)")
        }

        card.c_createdOn = Date()
        card.c_nextReview = Date()
        card.cardDeckNumber = nextNumber
        card.cardIdentifier = identifier
        card.partOfList = false
        card.passes = 0
        card.prevSuccess = false
        card.reviewsLeft = Int16(deck.reviewAmount)
        card.totalPasses = 0
        card.type = kind.rawValue
        card.card_of = deck

        do {
            try viewContext.save()
            presentationMode.wrappedValue.dismiss()
        } catch {
            errorMessage = "Save failed: \(error.localizedDescription)"
        }
    }
}
