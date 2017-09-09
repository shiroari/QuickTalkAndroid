package io.b3.quicktalk.model

import com.google.common.collect.ImmutableList

class FileCardSet(header: CardSetHeader, cards: List<CardInstance>) : CardSet {

    override val id: String = header.id
    override val title: String = header.name
    override val count: Int
        get() = cards.size

    private val cards: List<CardInstance> = ImmutableList.copyOf(cards)

    override fun getCard(index: Int): Card {
        require(index < this.cards.size)
        return this.cards[index]
    }
}
