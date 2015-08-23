package com.snowcat.fajnyobed.Logic;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Snowcat on 22-Aug-15.
 */
public class CityFactory {

    public static ArrayList<City> fromJSON(JSONObject jsonObject) {
        ArrayList<City> cities = new ArrayList<>(jsonObject.length());
        for (int i = 0; i < jsonObject.length()-1; i++) {
            JSONObject cityJson = null;
            try {
                cityJson = jsonObject.getJSONObject(""+i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            City city = cityFromJSON(cityJson);
            if (city != null)
               cities.add(city);
        }
        return cities;
    }

    public static City cityFromJSON(JSONObject jsonObject) {
        City city = new City();
        try {
            city.id = jsonObject.getInt("id");
            city.name = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return city;
    }
}
