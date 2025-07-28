package com.belmontCrest.cardCrafter.supabase.model


/**
 * Our return codes for Supabase Controller functions.
 */
object ReturnValues {
    const val STATIC_NUM = Int.MIN_VALUE
    const val EMPTY_STRING = -100
    const val REPLACED_DECK = -1
    const val SUCCESS = 0
    const val NULL_USER = 1
    const val NULL_OWNER = 2
    const val NULL_CARDS = 3
    const val CC_LESS_THAN_20 = 4
    const val DECK_EXISTS = 6
    const val NOT_DECK_OWNER = 8
    const val BASIC_CT_ERROR = 10
    const val HINT_CT_ERROR = 11
    const val THREE_CT_ERROR = 12
    const val MULTI_CT_ERROR = 13
    const val NOTATION_CT_ERROR = 14
    const val CTD_ERROR = 27
    const val USER_NOT_FOUND = 28
    const val NULL_UPDATED_ON = 29
    const val UPDATED_ON_CONFLICT = 30
    const val NULL_DECK = 31
    const val MERGE_FAILED = 32
    const val EMPTY_CARD_LIST = 88
    const val NO_DECKS_TO_SYNC = 90
    const val UUID_CONFLICT = 101
    const val CANCELLED = 499
    const val NETWORK_ERROR = 500
    const val UNKNOWN_ERROR = 504
}
