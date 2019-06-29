package com.arseniy.maps.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.arseniy.maps.R
import com.arseniy.maps.data.CityAdapter
import com.arseniy.maps.data.MapData
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
            MapData.removeCityFromStorage(cityNamesList!![cityId]) //sharedPrefs
            cityNamesList.removeAt(cityId)
            (mAdapter as CityAdapter).data = cityNamesList //list
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.city_list)
        val recyclerView = findViewById<RecyclerView>(R.id.cityListView)

        recyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        mAdapter = CityAdapter(MapData.loadCityList())
        recyclerView.adapter = mAdapter

        //FAB
        val fab = findViewById<FloatingActionButton>(R.id.addCityFAB)
        fab.setOnClickListener { startActivityForResult(Intent(this, AddCityActivity::class.java), 0) }


        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == resultCode && intent != null) {
            val cityName = intent.getStringExtra("cityName")
            val cityNamesList = (mAdapter as CityAdapter).data
            cityNamesList!!.add(cityName)
            (mAdapter as CityAdapter).data = cityNamesList
        }
    }

}
