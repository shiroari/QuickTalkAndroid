package io.b3.quicktalk.dataprovider;

import java.util.List;

import io.b3.quicktalk.model.CardSetHeader;
import io.b3.quicktalk.model.FileCardSet;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public interface DataProvider {
    List<CardSetHeader> list();
    FileCardSet load(String uri, CardSetHeader header);
}
