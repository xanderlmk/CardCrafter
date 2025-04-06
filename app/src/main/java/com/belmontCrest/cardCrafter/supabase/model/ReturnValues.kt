package com.belmontCrest.cardCrafter.supabase.model


/**
 * Our return codes for Supabase Controller functions.
 */
object ReturnValues {
    const val EMPTY_STRING = -100
    const val REPLACED_DECK = -1
    const val SUCCESS = 0
    const val NULL_USER = 1
    const val NULL_OWNER = 2
    const val CC_LESS_THAN_20 = 4
    const val DECK_EXISTS = 6
    const val NOT_DECK_OWNER = 8
    const val EMPTY_CARD_LIST = 88
    const val UUID_CONFLICT = 101
    const val CANCELLED = 499
    const val NETWORK_ERROR = 500
    const val UNKNOWN_ERROR = 504
}