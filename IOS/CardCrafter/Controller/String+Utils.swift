//
//  String+Utils.swift
//  CardCrafter
//
//  Created by Assykilla on 7/30/25.
//

extension String {
    /** Whether the string is null, isEmpty, or contains only whitespace characters (such as spaces, tabs, or newlines) */
    var isBlank: Bool {
        trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
    /** Whether the string is NOT null, isNotEmpty, and DOES NOT contain only whitespace characters (such as spaces, tabs, or newlines) */
    var isNotBlank: Bool {
        !trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
    /** The opposite of isEmpty, if the string has any charcter (including whitespace chacters).
        To check for whitespace characters use isNotBlank*/
    var isNotEmpty: Bool {
        !isEmpty
    }
}
