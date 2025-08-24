package com.belmontCrest.cardCrafter.model

/**
 * Primitive types
 * @param BASIC
 * @param HINT
 * @param THREE
 * @param MULTI
 * @param CREATE_NEW This type is used to create new card types
 */
object Type {
    const val BASIC = "basic"
    const val HINT = "hint"
    const val THREE = "three"
    const val MULTI = "multi"
    const val NOTATION = "notation"
    const val CREATE_NEW = "create_new"
}

fun String.isPrimitiveType(): Boolean {
    val lowerCased = this.lowercase()
    return lowerCased == Type.BASIC || lowerCased == Type.HINT || lowerCased == Type.NOTATION ||
            lowerCased == Type.CREATE_NEW || lowerCased == Type.MULTI || lowerCased == Type.THREE
}