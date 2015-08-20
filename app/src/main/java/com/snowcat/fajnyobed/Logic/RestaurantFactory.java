package com.snowcat.fajnyobed.Logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Snowcat on 26.7.2015.
 */
public class RestaurantFactory {

    public static ArrayList<Restaurant> fromJSON(JSONObject jsonObject) {
        ArrayList<Restaurant> restaurants = new ArrayList<>(jsonObject.length());
        for (int i = 0; i < restaurants.size(); i++) {
            JSONObject restaurantJson = null;
            try {
                restaurantJson = jsonObject.getJSONObject(Integer.toString(i));
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
            Restaurant restaurant = restaurantFromJSON(restaurantJson);
            if (restaurant != null)
                restaurants.add(restaurant);
        }
        return restaurants;
    }

    public static Restaurant restaurantFromJSON(JSONObject jsonObject) {
        Restaurant restaurant = new Restaurant();
        try {
            restaurant.id = jsonObject.getInt("restaurant_id");
            restaurant.name = jsonObject.getString("name");
            restaurant.description = jsonObject.getString("description");
            restaurant.street = jsonObject.getString("street");
            restaurant.cityId = jsonObject.getInt("city_id");
            restaurant.city = jsonObject.getString("city");
            restaurant.rating = jsonObject.getInt("rating");
            restaurant.openHours = getOpenHours(jsonObject.getJSONObject("open"));
            restaurant.promoPhotos = getPromoPhotos(jsonObject.getJSONObject("promo_foto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurant;
    }

    public static ArrayList<String> getOpenHours(JSONObject hours) throws JSONException {
        ArrayList<String> openHours = new ArrayList<>();
        openHours.add(hours.getString("mon"));
        openHours.add(hours.getString("tue"));
        openHours.add(hours.getString("wed"));
        openHours.add(hours.getString("thu"));
        openHours.add(hours.getString("fri"));
        openHours.add(hours.getString("sat"));
        openHours.add(hours.getString("sun"));
        return openHours;
    }

    public static ArrayList<String> getPromoPhotos(JSONObject photos) throws JSONException {
        ArrayList<String> photoURLs = new ArrayList<>();
        photoURLs.add(photos.getString("380x200"));
        return photoURLs;
    }
}
