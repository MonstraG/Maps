package com.company.maps.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.company.maps.R
import com.company.maps.activities.city.AddCityActivity
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
        recyclerView.layoutManager =  LinearLayoutManager(this)
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
            (mAdapter as CityAdapter).data!!.add(intent.getStringExtra("cityName"))
        }
    }

}
