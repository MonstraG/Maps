package com.arseniy.maps.activities;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arseniy.maps.data.CityAdapter;
import com.arseniy.maps.data.MapData;
import com.arseniy.maps.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_list);
        RecyclerView recyclerView = findViewById(R.id.cityListView);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new CityAdapter(MapData.loadCityList());
        recyclerView.setAdapter(mAdapter);

        //FAB
        FloatingActionButton fab = findViewById(R.id.addCityFAB);
        fab.setOnClickListener(view -> startActivityForResult(new Intent(this, AddCityActivity.class), 0));


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == resultCode) {
            final String cityName = data.getStringExtra("cityName");
            ArrayList<String> cityNamesList = ((CityAdapter)mAdapter).getData();
            cityNamesList.add(cityName);
            ((CityAdapter)mAdapter).setData(cityNamesList);
        }
    }

    private final ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
            final int cityId = viewHolder.getAdapterPosition();
            ArrayList<String> cityNamesList = ((CityAdapter)mAdapter).getData();
            MapData.removeCityFromStorage(cityNamesList.get(cityId)); //sharedPrefs
            cityNamesList.remove(cityId);
            ((CityAdapter)mAdapter).setData(cityNamesList); //list
        }
    };

}
