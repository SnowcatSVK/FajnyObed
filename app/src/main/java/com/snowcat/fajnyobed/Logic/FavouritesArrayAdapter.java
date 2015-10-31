package com.snowcat.fajnyobed.Logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.snowcat.fajnyobed.R;

import java.util.ArrayList;

/**
 * Created by Snowcat on 31-Oct-15.
 */
public class FavouritesArrayAdapter extends ArrayAdapter<Restaurant> {

    public FavouritesArrayAdapter(Context context, ArrayList<Restaurant> restaurants) {
        super(context, R.layout.item_favourite, restaurants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_favourite, parent, false);
            holder = new ViewHolder();
            holder.nameText = (TextView) view.findViewById(R.id.favourites_list_name_textView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.nameText.setText(getItem(position).name);
        return view;
    }

    static class ViewHolder {
        TextView nameText;
    }
}
