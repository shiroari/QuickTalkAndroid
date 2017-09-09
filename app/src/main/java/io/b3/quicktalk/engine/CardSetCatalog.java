package io.b3.quicktalk.engine;

import java.util.List;

import io.b3.quicktalk.model.CardSet;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public interface CardSetCatalog {

    int getCount();
    CardSet getCardSet(String id);
    CardSet getCardSet(int index);
    List<CardSet> getCardSets();
}
