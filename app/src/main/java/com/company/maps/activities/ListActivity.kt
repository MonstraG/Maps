package com.company.maps.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.company.maps.R
import com.company.maps.activities.city.AddCityActivity
import com.company.maps.activities.city.EditCityActivity
import com.company.maps.data.CityAdapter
import com.company.maps.data.MapData
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListActivity : AppCompatActivity() {
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val cityId = viewHolder.adapterPosition
            val cityNamesList = (mAdapter as CityAdapter).data
            val intent = Intent(this@ListActivity, EditCityActivity::class.java)
            startActivityForResult(intent.putExtra(CITY_NAME, cityNamesList!![cityId]), 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_list)

        val recyclerView = findViewById<RecyclerView>(R.id.cityListView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = CityAdapter(MapData.loadCityList())
        recyclerView.adapter = mAdapter

        //FAB
        findViewById<FloatingActionButton>(R.id.addCityFAB).setOnClickListener {
            startActivityForResult(Intent(this, AddCityActivity::class.java), 0)
        }

        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == resultCode && intent != null) {
            val newCityName = intent.getStringExtra(NEW_CITY_NAME)
            val oldCityName = intent.getStringExtra(OLD_CITY_NAME)
            if (newCityName != null && oldCityName != null) {
                val list = (mAdapter as CityAdapter).data!!
                list.remove(oldCityName)
                list.add(newCityName)
                mAdapter!!.notifyDataSetChanged()
            } else {
                if (newCityName == null) {
                    Log.w("ListActivity::onActivityResult", "Received correct result code, but NEW_CITY_NAME is null!")
                }
                if (oldCityName == null) {
                    Log.w("ListActivity::onActivityResult", "Received correct result code, but OLD_CITY_NAME is null!")
                }
            }
        }
    }

    companion object EditExtraStrings {
        const val CITY_NAME = "cityName"
        const val OLD_CITY_NAME = "oldCityName"
        const val NEW_CITY_NAME = "newCityName"
    }
}
