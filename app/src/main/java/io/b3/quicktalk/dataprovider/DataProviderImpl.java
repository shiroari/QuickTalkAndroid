package io.b3.quicktalk.dataprovider;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.common.io.LineReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.b3.quicktalk.model.CardInstance;
import io.b3.quicktalk.model.CardSetHeader;
import io.b3.quicktalk.model.CardSetType;
import io.b3.quicktalk.model.FileCardSet;

/**
 * @author Stas Sukhanov
 * @since 26.07.2016
 */
public class DataProviderImpl implements DataProvider {

    private Pattern METADATA_PATTERN = Pattern.compile("^(\\d+)(\\s?-\\s?(.*))?$");

    private AssetManager assets;

    public DataProviderImpl(Context context) {
        this.assets = context.getAssets();
    }

    private static class Metadata {
        String title;
        int index;
    }

    @Override
    public List<CardSetHeader> list() {

        List<CardSetHeader> list = new ArrayList<>();

        try {

            for (String line: readLines("data/dir.txt")) {
                Metadata md = parseLine(line);
                if (md == null) {
                    continue;
                }
                String id = String.format("file%d", md.index);
                String uri = String.format("data/d%d.txt", md.index);
                list.add(new CardSetHeader(id, md.title, CardSetType.File, uri));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public FileCardSet load(String uri, CardSetHeader header) {

        List<CardInstance> cards;

        try {

            List<String> backData = readLines(header.getUri());
            List<String> frontData = readLines(header.getUri().replace(".txt", "tr.txt"));

            int count = Math.min(backData.size(), frontData.size());
            cards = new ArrayList<>(count);

            for (int i = 0; i < count; i ++) {
                cards.add(new CardInstance(i, frontData.get(i), backData.get(i)));
            }

        } catch (Exception e) {
            Log.e("resources", "Can not read catalog", e);
            cards = Collections.emptyList();
        }

        return new FileCardSet(header, cards);
    }

    private Metadata parseLine(String line) {
        Matcher m = METADATA_PATTERN.matcher(line);
        if (m.find()) {
            Metadata md = new Metadata();
            md.index = Integer.parseInt(m.group(1));
            if (m.groupCount() == 3) {
                md.title = m.group(3);
            } else {
                md.title = m.group(1);
            }
            return md;
        }
        return null;
    }

    private List<String> readLines(String resource) {
        List<String> lines = new ArrayList<>();
        try (InputStream io = assets.open(resource)) {
            try (Reader reader = new InputStreamReader(io)) {
                LineReader lineReader = new LineReader(reader);
                String line;
                while ((line = lineReader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            Log.e("resources", "Can not read file: " + resource, e);
        }
        return lines;
    }
}
