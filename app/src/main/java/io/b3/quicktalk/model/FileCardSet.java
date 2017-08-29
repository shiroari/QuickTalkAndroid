package io.b3.quicktalk.model;

//enum CardSetError: ErrorType {
//        case InvalidCardIndex
//        case InvalidSettings
//        }

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shiroari
 * @since 26.07.2016
 */
public class FileCardSet implements CardSet {

    private String id;
    private String title;
    private List<CardInstance> cards;

    public FileCardSet(CardSetHeader header, List<CardInstance> cards){
        this.id = header.getId();
        this.title = header.getName();
        this.cards = new ArrayList<>(cards);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getCount() {
        return this.cards.size();
    }

    @Override
    public Card getCard(int index) {
        Preconditions.checkElementIndex(index, this.cards.size());
        return this.cards.get(index);
    }
}