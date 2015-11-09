package com.snowcat.fajnyobed.Logic;

import android.util.Log;

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
        for (int i = 0; i < jsonObject.length() - 1; i++) {
            JSONObject restaurantJson = null;
            try {
                Log.e("restaurantJSON", "" + jsonObject.getJSONObject("" + String.valueOf(i)).toString());
                restaurantJson = jsonObject.getJSONObject("" + i);
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
            restaurant.street = jsonObject.getString("street");
            restaurant.cityId = jsonObject.getInt("city_id");
            restaurant.city = jsonObject.getString("city");
            restaurant.rating = jsonObject.getInt("rating");
            restaurant.promoPhotos = getPromoPhotos(jsonObject.getJSONObject("promo_foto"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurant;
    }

    public static ArrayList<String> getOpenHours(JSONObject hours) throws JSONException {
        ArrayList<String> openHours = new ArrayList<>();
        String pondelok = "Pondelok:  " + hours.getString("mon");
        String utorok = "Utorok:  " + hours.getString("tue");
        String streda = "Streda:  " + hours.getString("wed");
        String stvrtok = "Štvrtok:  " + hours.getString("thu");
        String piatok = "Piatok:  " + hours.getString("fri");
        String sobota = "Sobota:  " + hours.getString("sta");
        String nedela = "Nedela:  " + hours.getString("sun");
        openHours.add(pondelok);
        openHours.add(utorok);
        openHours.add(streda);
        openHours.add(stvrtok);
        openHours.add(piatok);
        openHours.add(sobota);
        openHours.add(nedela);
        return openHours;
    }

    public static ArrayList<String> getPromoPhotos(JSONObject photos) throws JSONException {
        ArrayList<String> photoURLs = new ArrayList<>();
        photoURLs.add(photos.getString("200x200"));
        photoURLs.add(photos.getString("380x200"));
        photoURLs.add(photos.getString("1440x900"));
        return photoURLs;
    }

    public static ArrayList<String> getCards(JSONObject paycards) throws JSONException {
        ArrayList<String> cards = new ArrayList<>();
        if (paycards.getInt("diners_club") == 1)
            cards.add("diners_club");
        if (paycards.getInt("maestro") == 1)
            cards.add("maestro");
        if (paycards.getInt("mastercard") == 1)
            cards.add("mastercard");
        if (paycards.getInt("mastercard_electronic") == 1)
            cards.add("mastercard_electronic");
        if (paycards.getInt("v_pay") == 1)
            cards.add("v_pay");
        if (paycards.getInt("visa") == 1)
            cards.add("visa");
        if (paycards.getInt("visa_electronic") == 1)
            cards.add("visa_electronic");
        return cards;
    }

    public static ArrayList<String> getFoodTickets(JSONObject foodTickets) throws JSONException {
        ArrayList<String> cards = new ArrayList<>();
        if (foodTickets.getInt("sodexho") == 1)
            cards.add("sodexho");
        if (foodTickets.getInt("cheque_dejeuner") == 1)
            cards.add("cheque_dejeuner");
        if (foodTickets.getInt("doxx") == 1)
            cards.add("doxx");
        if (foodTickets.getInt("ticket_restaurant") == 1)
            cards.add("ticket_restaurant");
        if (foodTickets.getInt("vasa_stravovacia") == 1)
            cards.add("vasa_stravovacia");
        return cards;
    }

    public static Restaurant restaurantDetailsFromJSON(JSONObject jsonObject) {
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
            restaurant.menus = getDailyMenus(jsonObject.optJSONArray("daily_menu"));
            restaurant.paymentCards = getCards(jsonObject.getJSONObject("paycard"));
            restaurant.foodTickets = getFoodTickets(jsonObject.getJSONObject("foodticket"));
            Log.e("Zbehol", "RestaurantDetail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurant;
    }

    public static ArrayList<DailyMenu> getDailyMenus(JSONArray menusJSONArray) {
        if (menusJSONArray != null) {
            ArrayList<DailyMenu> dailyMenus
                    = new ArrayList<>();
            for (int i = 0; i < menusJSONArray.length(); i++) {
                DailyMenu menu = new DailyMenu();
                try {
                    JSONObject menuJSON = menusJSONArray.getJSONObject(i);
                    menu.date = menuJSON.getString("date");
                    menu.dateString = menuJSON.getString("date_string");
                    menu.groups = MenuFactory.menuFromJSON(menuJSON.getJSONArray("menu"));
                    dailyMenus.add(menu);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Log.e("Zbehol", "DailyMenus");
            return dailyMenus;
        }
        return null;
    }

    public static ArrayList<Restaurant> parseFavourites(JSONArray array) {
        ArrayList<Restaurant> favourites = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                favourites.add(restaurantFromJSON(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return favourites;
    }
}
