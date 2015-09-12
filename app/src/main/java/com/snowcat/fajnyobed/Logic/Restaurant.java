package com.snowcat.fajnyobed.Logic;

import java.util.ArrayList;

/**
 * Created by Snowcat on 26.7.2015.
 */
public class Restaurant {

    public int id;
    public String name;
    public String description;
    public String street;
    public int cityId;
    public String city;
    public int rating;
    public ArrayList<String> openHours;
    public ArrayList<String> promoPhotos;
    public ArrayList<DailyMenu> menus;

}
