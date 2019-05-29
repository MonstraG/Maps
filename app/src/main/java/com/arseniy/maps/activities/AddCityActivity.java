package com.arseniy.maps.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arseniy.maps.data.MapData;
import com.arseniy.maps.R;
import com.google.android.gms.maps.model.LatLng;


import java.io.IOException;
import java.util.List;

public class AddCityActivity extends AppCompatActivity {

    private EditText cityNameField;
    private EditText cityLatField;
    private EditText cityLngField;
    private Geocoder coder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_city);

        onMapBtnInit();

        cityNameField = findViewById(R.id.cityNameField);
        cityLatField = findViewById(R.id.cityLatField);
        cityLngField = findViewById(R.id.cityLngField);

        addCityBtnInit();
        coder = new Geocoder(this);
        getLatLngFromApiBtnInit();
    }

    private void onMapBtnInit() {
        Button onMapBtn = findViewById(R.id.onMapBtn);
        onMapBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, PickLocationOnMapActivity.class);
            try {
                final double cityLat = Double.parseDouble(cityLatField.getText().toString());
                final double cityLng = Double.parseDouble(cityLngField.getText().toString());
                intent.putExtra("startingLat", cityLat);
                intent.putExtra("startingLng", cityLng);
            } catch (Exception ignored) {}
            startActivityForResult(intent, 0);
        });
    }

    @SuppressLint("SetTextI18n") //that will add commas instead of dots.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == resultCode) {
            final float lat = data.getFloatExtra("lat", 0f);
            final float lng = data.getFloatExtra("lng", 0f);
            cityLatField.setText(Float.toString(lat));
            cityLngField.setText(Float.toString(lng));
        }
    }

    private void addCityBtnInit() {
        Button addCityBtn = findViewById(R.id.addCityBtn);
        addCityBtn.setOnClickListener(view -> {
            try {
                final String cityName = cityNameField.getText().toString();
                final float cityLat = Float.parseFloat(cityLatField.getText().toString());
                final float cityLng = Float.parseFloat(cityLngField.getText().toString());
                storeCity(cityName, cityLat, cityLng);
                this.finish();
            } catch (Exception e) {
                setResult(-1);
                Toast.makeText(this, "Ошибка создания записи, проверьте данные.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeCity(String name, float lat, float lng) {
        MapData.addCityToStorage(name, lat, lng); //sharedPrefs
        setResult(0, new Intent().putExtra("cityName", name)); //list
    }

    @SuppressLint("SetTextI18n") //that will add commas instead of dots.
    private void getLatLngFromApiBtnInit() {
        Button getLatLngFromApiBtn = findViewById(R.id.getLatLngFromApiBtn);
        getLatLngFromApiBtn.setOnClickListener(view -> {
            LatLng pos = getLocationFromAddress(cityNameField.getText().toString());
            if (pos != null) {
                String lat = Double.toString(pos.latitude);
                if (lat.length() > 7)
                    lat = lat.substring(0, 7);
                cityLatField.setText(lat);
                String lng = Double.toString(pos.longitude);
                if (lng.length() > 7)
                    lng = lng.substring(0, 7);
                cityLngField.setText(lng);

                //show on map
                Intent intent = new Intent(this, PickLocationOnMapActivity.class);
                try {
                    final double cityLat = Double.parseDouble(cityLatField.getText().toString());
                    final double cityLng = Double.parseDouble(cityLngField.getText().toString());
                    intent.putExtra("startingLat", cityLat);
                    intent.putExtra("startingLng", cityLng);
                } catch (Exception ignored) {}
                startActivityForResult(intent, 0);
            }
        });
    }

    private LatLng getLocationFromAddress(String strAddress){
        try {
            List<Address> address = coder.getFromLocationName(strAddress,1);
            if (address != null && address.size() > 0) {
                Address location = address.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException ignored) { }
        Toast.makeText(this, "Нет интернет-соединения или такой город не найден.", Toast.LENGTH_SHORT).show();
        return null;
    }
}
