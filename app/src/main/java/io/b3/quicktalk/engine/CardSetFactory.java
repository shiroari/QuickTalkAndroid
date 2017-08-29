package io.b3.quicktalk.engine;

import javax.inject.Inject;

import io.b3.quicktalk.dataprovider.DataProvider;
import io.b3.quicktalk.model.CardSet;
import io.b3.quicktalk.model.CardSetHeader;

/**
 * @author shiroari
 * @since 26.07.2016
 */
public class CardSetFactory {

    private DataProvider dataProvider;

    @Inject
    public CardSetFactory(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public CardSet newCardSet(CardSetHeader header){
        switch (header.getType()) {
            case File:
                return dataProvider.load(header.getUri(), header);
        }
        throw new RuntimeException("Type not found: " + header.getType());
    }

}
