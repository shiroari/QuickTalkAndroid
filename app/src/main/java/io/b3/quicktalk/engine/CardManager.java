package io.b3.quicktalk.engine;

import java.util.Observer;

import io.b3.quicktalk.model.CardSet;

/**
 * @author shiroari
 * @since 26.07.2016
 */
public interface CardManager {

    enum CardState {
        Title, Front, Back
    }

    String getTitle();
    String getText();
    CardState getState();
    int getCurrent();
    int getCount();
    void start(CardSet cardset);
    void resume(CardSet cardset, int index);
    void restart();
    boolean nextStep();
    boolean previousStep();

    void addObserver(Observer observer);
    void deleteObserver(Observer observer);
}
