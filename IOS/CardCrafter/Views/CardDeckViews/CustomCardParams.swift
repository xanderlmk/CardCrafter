//
//  CustomCardParams.swift
//  CardCrafter
//
//  Created by Assykilla on 8/23/25.
//

import SwiftUI

struct FrontChoiceView: View {
    let choice: String
    let clickedChoice: Character
    let letter: Character
    var onClick: () -> Void

    @Environment(\.colorScheme) private var colorScheme

    var body: some View {
        Text(choice)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.vertical, 8)
            .padding(.horizontal, 12)
            .background(backgroundColor)
            .foregroundColor(foregroundColor)
            .cornerRadius(8)
            .contentShape(Rectangle())
            .onTapGesture { onClick() }
    }

    private var defaultChoiceColor: Color {
        #if canImport(UIKit)
            return Color(UIColor.secondarySystemBackground)
        #elseif canImport(AppKit)
            return Color.accentColor.opacity(0.2)
        #else
            return Color.gray.opacity(0.15)
        #endif
    }
    private var selectedColor: Color {
        if colorScheme == .light {
            Color(red: 0.96, green: 0.93, blue: 0.86)
        } else {
            Color(red: 0.45, green: 0.36, blue: 0.28)
        }
    }

    private var backgroundColor: Color {
        clickedChoice == letter ? selectedColor : defaultChoiceColor
    }

    private var foregroundColor: Color {
        if clickedChoice == letter {
            return colorScheme == .light ? Color.primary : Color.white
        } else {
            return Color.primary
        }
    }
}

struct BackChoiceView: View {
    @Environment(\.colorScheme) private var colorScheme

    let choice: String
    let correctChoice: Character
    let clickedChoice: Character
    let letter: Character
    var body: some View {
        Text(choice)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.vertical, 8)
            .padding(.horizontal, 12)
            .background(backgroundColor)
            .foregroundColor(foregroundColor)
            .cornerRadius(8)
            .contentShape(Rectangle())
    }
    private var defaultChoiceColor: Color {
        #if canImport(UIKit)
            return Color(UIColor.secondarySystemBackground)
        #elseif canImport(AppKit)
            // Use a neutral control background on macOS
            return Color.accentColor.opacity(0.2)
        #else
            return Color.gray.opacity(0.15)
        #endif
    }

    private var selectedColor: Color {
        if colorScheme == .light {
            Color(red: 0.96, green: 0.93, blue: 0.86)
        } else {
            Color(red: 0.45, green: 0.36, blue: 0.28)
        }
    }
    private var correctColor: Color {
        if colorScheme == .light {
            Color(red: 0.353, green: 0.706, blue: 0.353)
        } else {
            Color(red: 0.039, green: 0.235, blue: 0.039)
        }
    }

    private var backgroundColor: Color {
        if clickedChoice == letter && clickedChoice != correctChoice {
                return selectedColor
            }
            // 2) this letter is the correct answer -> correct color
            if correctChoice == letter {
                return correctColor
            }
            // 3) otherwise default
            return defaultChoiceColor
    }

    private var foregroundColor: Color {
        // when selected use readable contrast; otherwise system primary
        if clickedChoice == letter {
            return colorScheme == .light ? Color.primary : Color.white
        } else {
            return Color.primary
        }
    }
}
