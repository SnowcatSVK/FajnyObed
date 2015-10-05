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
 * Created by Snowcat on 03-Oct-15.
 */
public class OpenHoursAdapter extends ArrayAdapter<String> {

    public OpenHoursAdapter(Context context, ArrayList<String> strings) {
        super(context, R.layout.item_open_hour, strings);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_open_hour, parent, false);
            holder = new ViewHolder();
            holder.hourText = (TextView) view.findViewById(R.id.hour_textView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.hourText.setText(getItem(position));
        return view;
    }

    static class ViewHolder {
        TextView hourText;
    }
}
