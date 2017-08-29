package io.b3.quicktalk.engine;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.b3.quicktalk.dataprovider.DataProvider;
import io.b3.quicktalk.model.CardSet;
import io.b3.quicktalk.model.CardSetHeader;

/**
 * @author shiroari
 * @since 26.07.2016
 */
public class CardSetCatalogImpl implements CardSetCatalog {

    private DataProvider dataProvider;
    private CardSetFactory factory;
    private Map<String, CardSet> localSets = new HashMap<>();
    private List<CardSet> localSetsArray = new ArrayList<>();

    @Inject
    public CardSetCatalogImpl(DataProvider dataProvider, CardSetFactory factory) {
        this.factory = factory;
        this.dataProvider = dataProvider;
        findLocalSets();
    }

    private void findLocalSets() {
        for (CardSetHeader header: dataProvider.list()) {
            CardSet cardset = factory.newCardSet(header);
            localSets.put(header.getId(), cardset);
            localSetsArray.add(cardset);
        }
    }

    @Override
    public int getCount() {
        return localSets.size();
    }

    @Override
    public CardSet getCardSet(String id) {
        return localSets.get(id);
    }

    @Override
    public CardSet getCardSet(int index) {
        return localSetsArray.get(index);
    }

    @Override
    public List<CardSet> getCardSets() {
        return ImmutableList.copyOf(localSetsArray);
    }
}
