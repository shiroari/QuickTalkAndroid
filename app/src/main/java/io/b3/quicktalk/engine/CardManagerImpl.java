package io.b3.quicktalk.engine;

import android.util.Log;

import java.util.Observable;

import javax.inject.Inject;

import io.b3.quicktalk.model.Card;
import io.b3.quicktalk.model.CardSet;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public class CardManagerImpl extends Observable implements CardManager {

    private enum CardSide {
        Front, Back
    }

    private String text = "";
    private CardState state = CardState.Front;
    private CardSide cardSide = CardSide.Front;
    private CardSet cardSet;
    private int cardIndex = -1;

    @Inject
    public CardManagerImpl() {

    }

    @Override
    public String getTitle() {
        return cardSet.getTitle();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public CardState getState() {
        return state;
    }

    @Override
    public int getCurrent() {
        return cardIndex;
    }

    @Override
    public int getCount() {
        return cardSet.getCount();
    }

    @Override
    public void start(CardSet newCardSet) {
        start(newCardSet, -1);
    }

    @Override
    public void resume(CardSet newCardSet, int cardIndex) {
        start(newCardSet, cardIndex);
    }

    @Override
    public void restart() {
        start(this.cardSet);
    }

    private void start(CardSet cardSet, int cardIndex) {
        this.cardSet = cardSet;
        this.cardIndex = cardIndex;
        this.cardSide = CardSide.Front;
        update();
    }

    @Override
    public boolean nextStep() {

        if (cardIndex == -1 || cardSide == CardSide.Back) {
            if (cardIndex + 1 >= cardSet.getCount()){
                return false;
            }
            cardIndex += 1;
            cardSide = CardSide.Front;
        } else {
            cardSide = CardSide.Back;
        }

        if (shouldSkip()){
            return nextStep();
        }

        update();

        return true;
    }

    @Override
    public boolean previousStep() {

        if (cardSide == CardSide.Front) {
            if (cardIndex - 1 < 0) {
                return false;
            }
            cardIndex -= 1;
            cardSide = CardSide.Back;
        } else {
            cardSide = CardSide.Front;
        }

        if (shouldSkip()){
            return previousStep();
        }

        update();

        return true;
    }

    private boolean shouldSkip() {
        Card card = cardSet.getCard(cardIndex);
        String nextVal = (cardSide == CardSide.Back) ? card.getBackText() : card.getFrontText();
        return nextVal.equals(text);
    }

    private void update() {
        Log.d("CardManager", "update");
        updateModel();
        setChanged();
        notifyObservers();
    }

    private void updateModel() {

        if (cardSet == null) {
            this.text = "";
            this.state = CardState.Title;
            return;
        }

        if (cardIndex == -1) {
            this.text = cardSet.getTitle();
            this.state = CardState.Title;
            return;
        }

        Card card = cardSet.getCard(cardIndex);
        if (cardSide == CardSide.Back) {
            this.text = card.getBackText();
            this.state = CardState.Back;
        } else if (cardSide == CardSide.Front) {
            this.text = card.getFrontText();
            this.state = CardState.Front;
        }
    }
}
