package com.snowcat.fajnyobed.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.snowcat.fajnyobed.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Snowcat on 31-Oct-15.
 */
public class FavouritesArrayAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<FavouriteRestaurant> restaurants;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;

    public FavouritesArrayAdapter(Context context, ArrayList<FavouriteRestaurant> restaurants) {
        this.restaurants = restaurants;
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .showImageOnLoading(context.getResources().getDrawable(R.drawable.sad_face_50dp))
                .displayer(new SimpleBitmapDisplayer())
                .build();
    }


    @Override
    public int getGroupCount() {
        return restaurants.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (restaurants.get(groupPosition).menuForToday != null)
            return restaurants.get(groupPosition).menuForToday.size();
        else return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return restaurants.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return restaurants.get(groupPosition).menuForToday.get(childPosition);
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
            convertView = infalInflater.inflate(R.layout.item_favourite_group, parent, false);
        }
        String name = restaurants.get(groupPosition).name;
        TextView textView = (TextView) convertView.findViewById(R.id.restaurant_list_name_textView);
        textView.setText(name);

        TextView streetTextView = (TextView) convertView.findViewById(R.id.restaurant_list_place_textView);
        streetTextView.setText(restaurants.get(groupPosition).street);

        View view = convertView.findViewById(R.id.divider);
        if (groupPosition == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

        if (getChildrenCount(groupPosition) == 0){
            TextView desc = (TextView) convertView.findViewById(R.id.daily_menu_textView);
            desc.setVisibility(View.VISIBLE);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.restaurant);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage(restaurants.get(groupPosition).promoPhotos.get(2), imageView, options, animateFirstListener);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String foodName = ((Food) getChild(groupPosition, childPosition)).foodName;
        double foodPrice = ((Food) getChild(groupPosition, childPosition)).foodPrice;
        LayoutInflater infalInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.item_food_favourites, parent, false);
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

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 0);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
