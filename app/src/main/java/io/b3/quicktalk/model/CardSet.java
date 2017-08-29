package io.b3.quicktalk.model;

/**
 * @author shiroari
 * @since 26.07.2016
 */
public interface CardSet {
    String getId();
    String getTitle();
    int getCount();
    Card getCard(int index);
}
