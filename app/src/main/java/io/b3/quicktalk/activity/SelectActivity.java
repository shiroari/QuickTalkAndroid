package io.b3.quicktalk.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.b3.quicktalk.AppContext;
import io.b3.quicktalk.activity.base.CompatListActivity;
import io.b3.quicktalk.engine.CardSetCatalog;
import io.b3.quicktalk.engine.Tutor;
import io.b3.quicktalk.model.CardSet;

public class SelectActivity extends CompatListActivity {

    @Inject
    Tutor tutor;

    @Inject
    CardSetCatalog catalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppContext.inject(this);

        List<String> list = new ArrayList<>(catalog.getCount());
        for (CardSet cardSet: catalog.getCardSets()) {
            list.add(cardSet.getTitle());
        }

        setListAdapter(new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                list));

        setupActionBar();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        tutor.start(position);
        NavUtils.navigateUpFromSameTask(this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
