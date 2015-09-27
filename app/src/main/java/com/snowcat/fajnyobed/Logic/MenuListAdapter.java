package com.snowcat.fajnyobed.Logic;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.snowcat.fajnyobed.R;

import java.util.ArrayList;

/**
 * Created by Snowcat on 12-Sep-15.
 */
public class MenuListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<FoodGroup> groups;

    public MenuListAdapter(Context context, ArrayList<FoodGroup> groups) {
        this.groups = groups;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).meals.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).meals.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_group, null);
        }
        String udata=groups.get(groupPosition).groupName;
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        TextView textView = (TextView) convertView.findViewById(R.id.group_name_textView);
        textView.setText(udata);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String foodName = ((Food) getChild(groupPosition, childPosition)).foodName;
        double foodPrice = ((Food) getChild(groupPosition, childPosition)).foodPrice;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_food, null);
        }
        TextView nameTextView = (TextView) convertView.findViewById(R.id.food_name_textView);
        nameTextView.setText(foodName);
        TextView priceTextView = (TextView) convertView.findViewById(R.id.food_price_textView);
        if (foodPrice > 0)
            priceTextView.setText(String.valueOf(foodPrice) + "€");
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
