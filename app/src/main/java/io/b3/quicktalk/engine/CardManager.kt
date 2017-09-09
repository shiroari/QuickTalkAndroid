package io.b3.quicktalk.engine

import android.util.Log
import io.b3.quicktalk.model.CardSet
import java.util.Observer
import java.util.Observable

interface CardManager {

    enum class CardState {
        Title, Front, Back
    }

    val title: String
    val text: String
    val state: CardState
    val current: Int
    val count: Int

    fun start(newCardSet: CardSet)
    fun resume(newCardSet: CardSet, cardIndex: Int)
    fun restart()
    fun nextStep(): Boolean
    fun previousStep(): Boolean

    fun addObserver(observer: Observer)
    fun deleteObserver(observer: Observer)
}

class CardManagerImpl : Observable(), CardManager {

    private enum class CardSide {
        Front, Back
    }

    private var _text = ""
    private var _state = CardManager.CardState.Front
    private var _current = -1

    override var text
        get() = _text
        private set(value) {
            _text = value
        }

    override var state
        get() = _state
        private set(value) {
            _state = value
        }

    override var current
        get() = _current
        private set(value) {
            _current = value
        }

    private var cardSide = CardSide.Front
    private var cardSet: CardSet? = null

    // FIXME: remove nulls
    override val title: String
        get() = cardSet!!.title

    override val count: Int
        get() = cardSet!!.count

    override fun start(newCardSet: CardSet) {
        start(newCardSet, -1)
    }

    override fun resume(newCardSet: CardSet, cardIndex: Int) {
        start(newCardSet, cardIndex)
    }

    override fun restart() {
        start(this.cardSet!!)
    }

    private fun start(cardSet: CardSet, cardIndex: Int) {
        this.cardSet = cardSet
        this.current = cardIndex
        this.cardSide = CardSide.Front
        update()
    }

    override fun nextStep(): Boolean {

        if (current == -1 || cardSide == CardSide.Back) {
            if (current + 1 >= cardSet!!.count) {
                return false
            }
            current += 1
            cardSide = CardSide.Front
        } else {
            cardSide = CardSide.Back
        }

        if (shouldSkip()) {
            return nextStep()
        }

        update()

        return true
    }

    override fun previousStep(): Boolean {

        if (cardSide == CardSide.Front) {
            if (current - 1 < 0) {
                return false
            }
            current -= 1
            cardSide = CardSide.Back
        } else {
            cardSide = CardSide.Front
        }

        if (shouldSkip()) {
            return previousStep()
        }

        update()

        return true
    }

    private fun shouldSkip(): Boolean {
        val card = cardSet!!.getCard(current)
        val nextVal = if (cardSide == CardSide.Back) card.backText else card.frontText
        return nextVal == text
    }

    private fun update() {
        Log.d("CardManager", "update")
        updateModel()
        setChanged()
        notifyObservers()
    }

    private fun updateModel() {

        if (cardSet == null) {
            this.text = ""
            this.state = CardManager.CardState.Title
            return
        }

        if (current == -1) {
            this.text = cardSet!!.title
            this.state = CardManager.CardState.Title
            return
        }

        val card = cardSet!!.getCard(current)
        if (cardSide == CardSide.Back) {
            this.text = card.backText
            this.state = CardManager.CardState.Back
        } else if (cardSide == CardSide.Front) {
            this.text = card.frontText
            this.state = CardManager.CardState.Front
        }
    }
}