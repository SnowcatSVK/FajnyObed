package com.snowcat.fajnyobed.Logic;

import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Snowcat on 12-Sep-15.
 */
public class MenuFactory {

    public static ArrayList<FoodGroup> menuFromJSON(JSONArray menuJSON) throws JSONException {
        ArrayList<Food> meals = new ArrayList<>();
        for (int i = 0; i < menuJSON.length(); i++) {
            JSONObject foodObject = menuJSON.getJSONObject(i);
            Food food = new Food();

            food.foodName = foodObject.getString("name");
            food.foodName = Html.fromHtml(food.foodName).toString();
            food.foodPrice = foodObject.optDouble("price", 0.0);
            food.desc = foodObject.optString("description",null);
            food.seq = foodObject.optInt("seq",meals.size());
            if (food.desc != null && food.desc.length()<2) {
                Log.e("Food desc", food.desc + " bude null");
                food.desc = null;
            }
            if (foodObject.isNull("category")) {
                //Log.e("Zbehol", "category je null");
                food.foodType = null;
            } else {
                food.foodType = foodObject.getString("category");
                food.foodType = Html.fromHtml(food.foodType).toString();
            }
            meals.add(food.seq,food);
        }
        //Log.e("Zbehol", "MenuFactoryMenuFromJSON");
        return createGroups(meals);
    }

    public static ArrayList<FoodGroup> createGroups(ArrayList<Food> meals) {
        ArrayList<FoodGroup> groups = new ArrayList<>();
        for (Food food : meals) {
            if (food.foodType != null) {
                //Log.e("Zbehol", food.foodType);
                if (groups.size() == 0) {
                    FoodGroup group = new FoodGroup();
                    group.groupName = food.foodType;
                    group.meals = new ArrayList<>();
                    group.meals.add(food);
                    groups.add(group);
                } else {
                    int i = 0;
                    for (FoodGroup group : groups) {
                        if (!group.groupName.equalsIgnoreCase(food.foodType) && i == groups.size() - 1) {
                            FoodGroup newGroup = new FoodGroup();
                            newGroup.groupName = food.foodType;
                            newGroup.meals = new ArrayList<>();
                            newGroup.meals.add(food);
                            groups.add(newGroup);
                        }
                        if (group.groupName.equalsIgnoreCase(food.foodType)) {
                            group.meals.add(food);
                            break;
                        }
                        i++;
                    }
                }
            }
        }
        if (groups.size() == 0) {
            FoodGroup group = new FoodGroup();
            group.groupName = "Denné menu";
            group.meals = meals;
            groups.add(group);
        }
        //Log.e("Zbehol", "MenuFactoryCreateGroups");
        return groups;
    }

    public static ArrayList<Food> menuFromJSONNoGroups(JSONArray menuJSON) throws JSONException {
        ArrayList<Food> meals = new ArrayList<>();
        for (int i = 0; i < menuJSON.length(); i++) {
            JSONObject foodObject = menuJSON.getJSONObject(i);
            Food food = new Food();

            food.foodName = foodObject.getString("name");
            food.foodName = Html.fromHtml(food.foodName).toString();
            food.foodPrice = foodObject.optDouble("price", 0.0);
            food.desc = foodObject.optString("description",null);
            food.seq = foodObject.optInt("seq",meals.size());
            if (food.desc != null && food.desc.length()<2) {
                Log.e("Food desc", food.desc + " bude null");
                food.desc = null;
            }
            if (foodObject.isNull("category")) {
                //Log.e("Zbehol", "category je null");
                food.foodType = null;
            } else {
                food.foodType = foodObject.getString("category");
                food.foodType = Html.fromHtml(food.foodType).toString();
            }
            meals.add(food.seq,food);
        }
        //Log.e("Zbehol", "MenuFactoryMenuFromJSON");
        return meals;
    }
}
