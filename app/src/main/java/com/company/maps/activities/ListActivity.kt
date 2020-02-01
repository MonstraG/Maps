package com.company.maps.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.maps.R
import com.company.maps.activities.city.AddCityActivity
import com.company.maps.activities.city.EditCityActivity
import com.company.maps.data.MapData
import com.company.maps.data.item.COUNTRY
import com.company.maps.data.item.ItemAdapter
import com.company.maps.logger.Logger
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val cityId = viewHolder.adapterPosition
            val itemAdapter = mAdapter as ItemAdapter

            if (itemAdapter.getItemViewType(cityId) == COUNTRY) {
                return
            }

            val city = itemAdapter.get(cityId)
            val intent = Intent(this@ListActivity, EditCityActivity::class.java)
            startActivityForResult(intent.putExtra(CITY_NAME, city.name), 0)
        }

        override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            if (viewHolder.itemViewType == COUNTRY) {
                return 0
            }
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.log("Created")
        setContentView(R.layout.city_list)

        //init city list view
        recyclerView = findViewById(R.id.cityListView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)

        loadCities(recyclerView!!)

        //FAB
        findViewById<FloatingActionButton>(R.id.addCityFAB).setOnClickListener {
            startActivityForResult(Intent(this, AddCityActivity::class.java), 0)
        }

        //attach swiping controls
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView)
    }

    private fun loadCities(recyclerView: RecyclerView) {
        mAdapter = ItemAdapter(MapData(this).countries)
        recyclerView.adapter = mAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        Logger.logIntent(requestCode, resultCode, intent)
        loadCities(recyclerView!!)
    }

    companion object IntentExtraStrings {
        const val CITY_NAME = "cityName"
        const val OLD_CITY_NAME = "oldCityName"
        const val NEW_CITY_NAME = "newCityName"
    }
}
