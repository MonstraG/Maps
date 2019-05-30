package com.arseniy.maps.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;

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
        final int appFlags = context.getApplicationInfo().flags;
        final boolean isDebug = (appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        mapData = getDefaultSharedPreferences(context);
        gson = new Gson();

        if (isDebug)
            debugPrintAllData();

        transferStorageToJson();

        if (isDebug)
            debugPrintAllData();

        loadAndDraw();
    }

    private static LatLng getPosOldWay(String name) {
        return new LatLng(mapData.getFloat(name + "lat", 0f), mapData.getFloat(name + "lng", 0f));
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
            Log.d("MapData-Old", "Removed city" + name);
        } catch (Exception ignored) {
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
        Log.d("MapData-Old", "Added city" + name);
    }

    public static void removeCityFromStorage(String name) {
        try {
            mapDataEditor = mapData.edit();
            mapDataEditor.remove(name);
            ArrayList<String> cityNames = loadCityList();
            cityNames.remove(name);
            mapDataEditor.putStringSet("cityNames", new HashSet<>(cityNames));
            mapDataEditor.apply();
            Log.d("MapData", "Removed city" + name);
        } catch (Exception ignored) {
        }
    }

    public static LatLng getPos(String name) {
        City city = gson.fromJson(mapData.getString(name, ""), City.class);
        return city.getLatLng();
    }

    public static ArrayList<String> loadCityList() {
        return new ArrayList<>(Objects.requireNonNull(mapData.getStringSet("cityNames", new HashSet<>())));
    }

    private void transferStorageToJson() {
        ArrayList<String> cityNames = loadCityList();
        if (cityNames.size() > 0) {
            if (mapData.contains(cityNames.get(0) + "lat") || mapData.contains(cityNames.get(0) + "lng")) {
                cityNames.forEach(city -> {
                    LatLng pos = getPosOldWay(city);
                    removeCityFromStorageOld(city);
                    addCityToStorage(city, pos);
                });
            }
        }
    }

    private void debugPrintAllData() {
        Log.d("data", "ALL AVAILABLE DATA");
        mapData.getAll().entrySet().forEach(entry -> Log.d("data", entry.getKey() + " " + entry.getValue()));
        Log.d("data", "END OF AVAILABLE DATA");

    }

    private void loadAndDraw() {
        Set<String> map = mapData.getStringSet("cityNames", new HashSet<>());
        if (map != null) {
            map.forEach(name -> MapsActivity.drawCity(name, getPos(name), false));
        }
    }
}
