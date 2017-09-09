package io.b3.quicktalk.model;

//enum CardSetError: ErrorType {
//        case InvalidCardIndex
//        case InvalidSettings
//        }

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public class CardSetHeader {

    private String id;
    private String name;
    private CardSetType type;
    private String uri;

    public CardSetHeader(String id, String name, CardSetType type, String uri) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CardSetType getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }
}