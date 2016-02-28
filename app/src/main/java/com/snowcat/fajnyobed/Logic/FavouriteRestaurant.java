package com.snowcat.fajnyobed.Logic;

import java.util.ArrayList;

/**
 * Created by Snowcat on 28.02.2016.
 */
public class FavouriteRestaurant {

    public int id;
    public String name;
    public String street;
    public int cityId;
    public String city;
    public ArrayList<String> promoPhotos;
    public ArrayList<Food> menuForToday;
    public int positionInList;

    public FavouriteRestaurant() {

    }
}
