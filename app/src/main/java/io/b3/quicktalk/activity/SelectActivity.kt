package io.b3.quicktalk.activity

import android.R
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import io.b3.quicktalk.AppContext
import io.b3.quicktalk.activity.compat.CompatListActivity
import io.b3.quicktalk.engine.CardSetCatalog
import io.b3.quicktalk.engine.Tutor
import java.util.ArrayList
import javax.inject.Inject

class SelectActivity : CompatListActivity() {

    @Inject
    lateinit var tutor: Tutor

    @Inject
    lateinit var catalog: CardSetCatalog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContext.inject(this)

        val list = ArrayList<String>(catalog!!.count)
        for (cardSet in catalog!!.cardSets) {
            list.add(cardSet.title)
        }

        listAdapter = ArrayAdapter(this,
                R.layout.simple_list_item_1,
                R.id.text1,
                list)

        setupActionBar()
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        super.onListItemClick(listView, view, position, id)
        tutor!!.start(position)
        NavUtils.navigateUpFromSameTask(this)
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }
}