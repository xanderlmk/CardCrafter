//
//  AddCardView.swift
//  CardCrafter
//
//  Created by Assykilla on 7/28/25.
//
import SwiftUI
import CoreData

enum CardKind: String, CaseIterable, Identifiable {
    case basic, three, hint, multi
    var id: String { rawValue }
}

struct AddCardView: View {
    @ObservedObject var deck: Deck
    @Environment(\.managedObjectContext) private var viewContext
    
    @State private var kind: CardKind = .basic
    @State private var cd: CardDetails = CardDetails()
    @State private var errorMessage: String?
    
    private var text: String {
        cd.partOfQOrA ? "Question" : "Answer"
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
                    TextField("Question", text: $cd.question)
                    if kind == .three || kind == .hint {
                        TextField("Middle", text: $cd.middle)
                    }
                    if kind == .three {
                        Picker("Part of \(text)", selection: $cd.partOfQOrA) {
                            Text("Question").tag(true)
                            Text("Answer").tag(false)
                        }
                        .pickerStyle(.segmented)
                    }
                    if kind != .multi {
                        TextField("Answer", text: $cd.answer)
                    }
                    else {
                        TextField("ChoiceA", text: $cd.choiceA)
                        TextField("ChoiceB", text: $cd.choiceB)
                        TextField("ChoiceC", text: $cd.choiceC)
                        TextField("ChoiceD", text: $cd.choiceD)
                        
                        Menu {
                            Button("A") { cd.correct = "a" }
                            Button("B") { cd.correct = "b" }
                            Button("C") { cd.correct = "c" }
                            Button("D") { cd.correct = "d" }
                        } label: {
                            Label("Correct: \(cd.correct.uppercased())", systemImage: "chevron.down")
                        }
                        .padding()
                        
                    }
                }
                
                Section {
                    Button("Save") {
                        addCard()
                    }
                    .disabled(
                        cd.question.isEmpty ||
                        ((kind == .three || kind == .hint) && cd.middle.isBlank) ||
                        (kind != .multi && cd.answer.isBlank) ||
                        (kind == .multi && (cd.choiceA.isBlank || cd.choiceB.isBlank || cd.correct == "?"))
                    )
                    
                    if let error = errorMessage {
                        Text(error).foregroundColor(.red)
                    }
                }
            }
            .navigationTitle("New Card")
#if os(macOS)
            .padding()
#endif
        }
    }
    
    private func addCard() {
        // 1) Compute next cardDeckNumber & cardIdentifier
        let fetch: NSFetchRequest<Card> = Card.fetchRequest()
        fetch.predicate = NSPredicate(format: "deck == %@", deck)
        fetch.sortDescriptors = [NSSortDescriptor(key: "cardDeckNumber", ascending: false)]
        fetch.fetchLimit = 1
        
        let nextNumber: Int32
        if let last = (try? viewContext.fetch(fetch))?.first {
            nextNumber = last.cardDeckNumber + 1
        } else {
            nextNumber = 1
        }
        let identifier = "\(deck.d_uuid)-\(nextNumber)"
        
        switch kind {
        case .basic:
            guard cd.question.isNotBlank, cd.answer.isNotBlank else {
                return fillOutFields()
            }
        case .hint:
            guard cd.question.isNotBlank, cd.middle.isNotBlank, cd.answer.isNotBlank else {
                return fillOutFields()
            }
        case .three:
            guard cd.question.isNotBlank, cd.middle.isNotBlank, cd.answer.isNotBlank else {
                return fillOutFields()
            }
        case .multi:
            guard cd.question.isNotBlank, cd.choiceA.isNotBlank,
                  cd.choiceB.isNotBlank, ("a"..."d").contains(cd.correct) else {
                return fillOutFields()
            }
            guard !(cd.choiceC.isBlank && cd.choiceD.isNotBlank) else {
                return fillOutMulti(string: "Cannot skip choice C and fill choice D")
            }
            guard !(cd.choiceC.isBlank && cd.correct == "c"),
                  !(cd.choiceD.isBlank && cd.correct == "d") else {
                return fillOutMulti(string: "Answer can't be a blank choice")
            }
        }
        
        let card: Card
        switch kind.rawValue {
        case "basic":
            let c = BasicCard(context: viewContext)
            c.question = cd.question
            c.answer = cd.answer
            card = c
            
        case "three":
            let c = ThreeFieldCard(context: viewContext)
            c.question = cd.question
            c.middle = cd.middle
            c.answer = cd.answer
            c.partOfQorA = cd.partOfQOrA
            card = c
            
        case "hint":
            let c = HintCard(context: viewContext)
            c.question = cd.question
            c.hint = cd.middle
            c.answer = cd.answer
            card = c
        case "multi":
            let c = MultiChoiceCard(context: viewContext)
            c.question = cd.question
            c.choiceA = cd.choiceA
            c.choiceB = cd.choiceB
            c.choiceC = cd.choiceC.isBlank ? nil : cd.choiceC
            c.choiceD = cd.choiceD.isBlank ? nil : cd.choiceD
            c.correct = String(cd.correct)
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
        card.deck = deck
        
        do {
            try viewContext.save()
            resetFields()
            
        } catch let error as NSError {
            NSLog("Save failed with error: \(error.localizedDescription)")
            NSLog("UserInfo dump:\n\(error.userInfo)")
            errorMessage = "Save failed: \(error.localizedDescription)"
        }
    }
    private func resetFields() {
        cd = cd.resetDetails()
        errorMessage = ""
    }
    private func fillOutFields() {
        errorMessage = "Please fill out fields"
    }
    private func fillOutMulti(string: String) {
        errorMessage = string
    }
}
