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
            startActivityForResult(Intent(this@ListActivity,
                    EditCityActivity::class.java).putExtra("cityName", cityNamesList!![cityId]), 0)
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
        findViewById<FloatingActionButton>(R.id.addCityFAB).setOnClickListener {
            startActivityForResult(Intent(this, AddCityActivity::class.java), 0)
        }

        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == resultCode && intent != null) {
            val cityName = intent.getStringExtra("cityName")
            if (cityName != null) {
                (mAdapter as CityAdapter).data!!.add(cityName)
                mAdapter!!.notifyDataSetChanged()
            }
        }
    }
}
