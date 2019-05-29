package com.arseniy.maps.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.arseniy.maps.activities.MapsActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MapData {

    private static SharedPreferences.Editor mapDataEditor;
    private static SharedPreferences mapData;

    public MapData(Context context) {
        mapData = getDefaultSharedPreferences(context);
        mapDataEditor = mapData.edit();
        if (mapData.getBoolean("!firststart", true)) {
            mapDataEditor.clear();

            addCityToStorage("Екатеринбург", 56.8389, 60.6057);
            addCityToStorage("Каир", 30.0444, 31.2357);
            addCityToStorage("Москва", 55.7558, 37.6173);
            addCityToStorage("Лондон", 51.5180, -0.1753);
            addCityToStorage("Вена", 48.2082, 16.3738);
            addCityToStorage("Берн", 46.9480, 7.4474);
            addCityToStorage("Рим", 41.9099, 12.4964);
            addCityToStorage("Париж", 48.8588, 2.2770);
            addCityToStorage("Прага", 50.0595, 14.3255);
            addCityToStorage("Будапешт", 47.4811, 18.9902);

            mapDataEditor.putBoolean("!firststart", false);

            mapDataEditor.apply();
        }
        loadAndDraw();
    }

    public static void addCityToStorage(String name, double lat, double lng) {
        mapDataEditor.putFloat(name + "lat", (float)lat);
        mapDataEditor.putFloat(name + "lng", (float)lng);
        ArrayList<String> cityNames = loadCityList();
        cityNames.add(name);
        mapDataEditor.putStringSet("cityNames", new HashSet<>(cityNames));
        mapDataEditor.apply();
        Log.d("BuildCityCoordinates", "Stored city: " + name + " " + lat + " " + lng);
    }

    public static void removeCityFromStorage(String name) {
        try {
            mapDataEditor.remove(name + "lat");
            mapDataEditor.remove(name + "lng");
            ArrayList<String> cityNames = loadCityList();
            cityNames.remove(name);
            mapDataEditor.putStringSet("cityNames", new HashSet<>(cityNames));
            mapDataEditor.apply();
        } catch (Exception ignored) {}

    }


    private void loadAndDraw() {
        Set<String> map = mapData.getStringSet("cityNames", new HashSet<>());
        if (map != null) {
            map.forEach(name -> MapsActivity.drawCity(name, new LatLng(mapData.getFloat(name + "lat", 0f), mapData.getFloat(name + "lng", 0f)), false));
        }
    }

    public static ArrayList<String> loadCityList() {
        return new ArrayList<>(Objects.requireNonNull(mapData.getStringSet("cityNames", new HashSet<>())));
    }
}
