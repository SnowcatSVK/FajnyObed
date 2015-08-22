package com.snowcat.fajnyobed.Logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snowcat.fajnyobed.R;

import java.util.ArrayList;

/**
 * Created by Snowcat on 22-Aug-15.
 */
public class CityAdapter extends ArrayAdapter<City> {
    public CityAdapter(Context context, ArrayList<City> cities) {
        super(context, R.layout.item_city, cities);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_city, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.city_name_textView);
        textView.setText(getItem(position).name);
        return convertView;
    }
}
