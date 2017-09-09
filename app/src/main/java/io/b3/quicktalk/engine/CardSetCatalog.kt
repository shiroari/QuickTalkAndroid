package io.b3.quicktalk.engine

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import io.b3.quicktalk.dataprovider.DataProvider
import io.b3.quicktalk.model.CardSet
import io.b3.quicktalk.model.CardSetHeader
import io.b3.quicktalk.model.CardSetType
import javax.inject.Inject

interface CardSetCatalog {

    val count: Int
    val cardSets: List<CardSet>

    fun getCardSet(id: String): CardSet?
    fun getCardSet(index: Int): CardSet
}

class CardSetCatalogImpl @Inject
constructor(private val dataProvider: DataProvider, private val factory: CardSetFactory) : CardSetCatalog {

    private lateinit var localSets : Map<String, CardSet>
    private lateinit var localSetsArray : List<CardSet>

    init {
        findLocalSets()
    }

    private fun findLocalSets() {
        val localSets_ = ImmutableMap.builder<String, CardSet>()
        val localSetsArray_ = ImmutableList.builder<CardSet>()
        for (header in dataProvider.list()) {
            val cardset = factory.newCardSet(header)
            localSets_.put(header.id, cardset)
            localSetsArray_.add(cardset)
        }
        localSets = localSets_.build()
        localSetsArray = localSetsArray_.build()
    }

    override val count: Int
        get() = localSets.size

    override fun getCardSet(id: String): CardSet? = localSets[id]

    override fun getCardSet(index: Int): CardSet = localSetsArray[index]

    override val cardSets: List<CardSet>
        get() = localSetsArray
}

class CardSetFactory @Inject
constructor(private val dataProvider: DataProvider) {

    fun newCardSet(header: CardSetHeader): CardSet {
        when (header.type) {
            CardSetType.File -> return dataProvider.load(header.uri, header)
        }
        //throw RuntimeException("Type not found: " + header.type)
    }

}