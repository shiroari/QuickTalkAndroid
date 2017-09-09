package io.b3.quicktalk.model;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public class CardInstance implements Card {

    private int index;
    private String frontText;
    private String backText;

    public CardInstance(int index, String frontText, String backText){
        this.index = index;
        this.frontText = frontText;
        this.backText = backText;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getFrontText() {
        return frontText;
    }

    @Override
    public String getBackText() {
        return backText;
    }
}

