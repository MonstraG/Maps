package com.arseniy.maps.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.arseniy.maps.R;
import com.arseniy.maps.data.MapData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
        setContentView(R.layout.add_city);

        onMapBtnInit();

        cityNameField = findViewById(R.id.cityNameField);
        cityLatField = findViewById(R.id.cityLatField);
        cityLngField = findViewById(R.id.cityLngField);

        addCityBtnInit();
        coder = new Geocoder(this);
        getLatLngFromApiBtnInit();

        getFromCurLocBtnInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cityNameField.requestFocus();
    }

    private void onMapBtnInit() {
        Button onMapBtn = findViewById(R.id.onMapBtn);
        onMapBtn.setOnClickListener(view -> callOnMapPick());
    }

    private void  callOnMapPick() {
        Intent intent = new Intent(this, PickLocationOnMapActivity.class);
        try {
            final double cityLat = Double.parseDouble(cityLatField.getText().toString());
            final double cityLng = Double.parseDouble(cityLngField.getText().toString());
            intent.putExtra("startingLat", cityLat);
            intent.putExtra("startingLng", cityLng);
        } catch (Exception ignored) {
        }

        startActivityForResult(intent, 0);
    }

    @SuppressLint("SetTextI18n") //that will add commas instead of dots.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == resultCode) {
            final float lat = data.getFloatExtra("lat", 0f);
            final float lng = data.getFloatExtra("lng", 0f);
            setLatLntData(new LatLng(lat, lng));
        }
    }

    private void addCityBtnInit() {
        Button addCityBtn = findViewById(R.id.addCityBtn);
        addCityBtn.setOnClickListener(view -> {
            try {
                final String cityName = cityNameField.getText().toString().trim();
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
            LatLng pos = getLocationFromAddress(cityNameField.getText().toString().trim());
            if (pos != null) {
                setLatLntData(pos);
                callOnMapPick();
            }
        });
    }

    private LatLng getLocationFromAddress(String strAddress) {
        try {
            List<Address> address = coder.getFromLocationName(strAddress, 1);
            if (address != null && address.size() > 0) {
                Address location = address.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException ignored) {
        }

        Toast.makeText(this, "Нет интернет-соединения или такой город не найден.", Toast.LENGTH_SHORT).show();
        return null;
    }

    private void getFromCurLocBtnInit() {
        Button fromCurLocBtn = findViewById(R.id.fromCurLocBtn);
        fromCurLocBtn.setOnClickListener(view -> {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(result -> setLatLntData(new LatLng(result.getLatitude(), result.getLongitude())));
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(result -> setLatLntData(new LatLng(result.getLatitude(), result.getLongitude())));
            }
        } else {
            Toast.makeText(this, "Неудалось получить локацию - разрешение не выдано.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLatLntData(LatLng pos) {
        if (pos != null) {
            String lat = Double.toString(pos.latitude);
            if (lat.length() > 7)
                lat = lat.substring(0, 7);
            cityLatField.setText(lat);
            String lng = Double.toString(pos.longitude);
            if (lng.length() > 7)
                lng = lng.substring(0, 7);
            cityLngField.setText(lng);
        }
    }
}
