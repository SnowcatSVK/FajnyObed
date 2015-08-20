package com.snowcat.fajnyobed.Logic;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.snowcat.fajnyobed.R;

import java.util.ArrayList;

/**
 * Created by Snowcat on 20-Aug-15.
 */
public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurants)  {
        super(context, R.layout.item_restaurant, restaurants);
    }
}
