package com.arseniy.maps.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.arseniy.maps.activities.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MapData {

    private static SharedPreferences.Editor mapDataEditor;
    private static SharedPreferences mapData;
    private static Gson gson;

    public MapData(Context context) {
        mapData = getDefaultSharedPreferences(context);
        gson = new Gson();

        transferStorageToJson();

        loadAndDraw();
    }

    private void transferStorageToJson() {
        ArrayList<String> cityNames = loadCityList();
        if (mapData.contains(cityNames.get(0) + "lat") || mapData.contains(cityNames.get(0) + "lng")) {
            cityNames.forEach(city -> {
                LatLng pos = getPos(city);
                removeCityFromStorageOld(city);
                addCityToStorage(city, pos);
            });
        }
    }

    public static void addCityToStorage(String name, double lat, double lng) {
        addCityToStorage(name, new LatLng(lat, lng));
    }


    private static void addCityToStorage(String name, LatLng pos) {
        mapDataEditor = mapData.edit();
        City city = new City(name, pos);
        mapDataEditor.putString(name, gson.toJson(city));

        ArrayList<String> cityNames = loadCityList();
        cityNames.add(name);
        mapDataEditor.putStringSet("cityNames", new HashSet<>(cityNames));

        mapDataEditor.apply();
    }

    public static void removeCityFromStorage(String name) {
        try {
            mapDataEditor = mapData.edit();
            mapDataEditor.remove(name);
            ArrayList<String> cityNames = loadCityList();
            cityNames.remove(name);
            mapDataEditor.putStringSet("cityNames", new HashSet<>(cityNames));
            mapDataEditor.apply();
        } catch (Exception ignored) {}
    }

    private static void removeCityFromStorageOld(String name) {
        try {
            mapDataEditor = mapData.edit();
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
            map.forEach(name -> MapsActivity.drawCity(name, getPos(name), false));
        }
    }


    public static LatLng getPos(String name) {
        City city = gson.fromJson(mapData.getString(name, ""), City.class);
        return city.getlatLng();
    }

    public static ArrayList<String> loadCityList() {
        return new ArrayList<>(Objects.requireNonNull(mapData.getStringSet("cityNames", new HashSet<>())));
    }
}
