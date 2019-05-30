package com.arseniy.maps.data;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class City {
    private String name;
    private LatLng latLng;
    private Date dateTimeOfCreation;

    City(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
        this.dateTimeOfCreation = Calendar.getInstance().getTime();
    }

    public City(String name, float latitude, float longitude) {
        this.name = name;
        this.latLng = new LatLng(latitude, longitude);
        this.dateTimeOfCreation = Calendar.getInstance().getTime();

    }

    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latLng = new LatLng(latitude, longitude);
        this.dateTimeOfCreation = Calendar.getInstance().getTime();
    }

    //only for deserialization
    private City() {
    }

    private String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    LatLng getLatLng() {
        return latLng;
    }

    private double getLatitude() {
        return latLng.latitude;
    }

    private double getLongitude() {
        return latLng.longitude;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    private void setLatLng(double latitude, double longitude) {
        this.latLng = new LatLng(latitude, longitude);
    }

    public Date getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }

    private String serializeDateTimeOfCreation() {
        return DateFormat.getDateTimeInstance().format(dateTimeOfCreation);
    }

    private void deserializeDateTimeOfCreation(String serializedDateTime) {
        try {
            dateTimeOfCreation = DateFormat.getDateTimeInstance().parse(serializedDateTime);
        } catch (Exception e) {
            dateTimeOfCreation = Calendar.getInstance().getTime();
        }
    }


    private static class CityAdapter extends TypeAdapter<City> {

        @Override
        public void write(JsonWriter out, City value) throws IOException {
            out.beginObject();
            out.name("name").value(value.getName());
            out.name("latitude").value(value.getLatitude());
            out.name("longitude").value(value.getLongitude());
            out.name("dateTimeOfCreation").value(value.serializeDateTimeOfCreation());
            out.endObject();
        }

        @Override
        public City read(JsonReader in) throws IOException {
            in.beginObject();
            City city = new City();
            double latitude = 0.0;
            double longitude = 0.0;

            while (in.hasNext()) {
                String name = in.nextName();
                switch (name) {
                    case "name":
                        city.setName(in.nextString());
                        break;
                    case "latitude":
                        latitude = in.nextDouble();
                        break;
                    case "longitude":
                        longitude = in.nextDouble();
                        break;
                    case "dateTimeOfCreation":
                        city.deserializeDateTimeOfCreation(in.nextString());
                        break;
                    default:
                        Log.e("City deserialization", "couldn't map field name " + name);
                        break;
                }
            }
            in.endObject();

            city.setLatLng(latitude, longitude);

            return city;
        }
    }

}
