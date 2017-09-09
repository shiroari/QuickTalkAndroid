package io.b3.quicktalk.model

/**
 * @author Stas Sukhanov
 * @since 09.09.2017
 */

interface Card {
    val index: Int
    val frontText: String
    val backText: String
}

interface CardSet {
    val id: String
    val title: String
    val count: Int
    fun getCard(index: Int): Card
}

data class CardInstance(override val index: Int,
                        override val frontText: String,
                        override val backText: String) : Card


data class CardSetHeader(val id: String,
                         val name: String,
                         val type: CardSetType,
                         val uri: String)
