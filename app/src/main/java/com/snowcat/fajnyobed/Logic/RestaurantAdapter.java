package com.snowcat.fajnyobed.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
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
 * Created by Snowcat on 20-Aug-15.
 */
public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;


    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurants) {
        super(context, R.layout.item_restaurant, restaurants);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
                .showImageOnLoading(getContext().getResources().getDrawable(R.drawable.sad_face_50dp))
                .displayer(new SimpleBitmapDisplayer())
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_restaurant, parent, false);
            holder = new ViewHolder();
            holder.nameText = (TextView) view.findViewById(R.id.restaurant_list_name_textView);
            holder.streetText = (TextView) view.findViewById(R.id.restaurant_list_place_textView);
            holder.image = (ImageView) view.findViewById(R.id.restaurant);
            holder.starView0 = (ImageView) view.findViewById(R.id.restaurant_list_stars_imageView0);
            holder.starView1 = (ImageView) view.findViewById(R.id.restaurant_list_stars_imageView1);
            holder.starView2 = (ImageView) view.findViewById(R.id.restaurant_list_stars_imageView2);
            holder.starView3 = (ImageView) view.findViewById(R.id.restaurant_list_stars_imageView3);
            holder.starView4 = (ImageView) view.findViewById(R.id.restaurant_list_stars_imageView4);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.nameText.setText(getItem(position).name);
        holder.streetText.setText(getItem(position).street);
        ImageLoader.getInstance().displayImage(getItem(position).promoPhotos.get(0), holder.image, options, animateFirstListener);
        switch (getItem(position).rating) {
            case 0:
                holder.starView0.setImageResource(R.drawable.ic_grade);
                holder.starView1.setImageResource(R.drawable.ic_grade);
                holder.starView2.setImageResource(R.drawable.ic_grade);
                holder.starView3.setImageResource(R.drawable.ic_grade);
                holder.starView4.setImageResource(R.drawable.ic_grade);
                break;
            case 1:
                holder.starView0.setImageResource(R.drawable.ic_star_fill);
                holder.starView1.setImageResource(R.drawable.ic_grade);
                holder.starView2.setImageResource(R.drawable.ic_grade);
                holder.starView3.setImageResource(R.drawable.ic_grade);
                holder.starView4.setImageResource(R.drawable.ic_grade);
                break;
            case 2:
                holder.starView0.setImageResource(R.drawable.ic_star_fill);
                holder.starView1.setImageResource(R.drawable.ic_star_fill);
                holder.starView2.setImageResource(R.drawable.ic_grade);
                holder.starView3.setImageResource(R.drawable.ic_grade);
                holder.starView4.setImageResource(R.drawable.ic_grade);
                break;
            case 3:
                holder.starView0.setImageResource(R.drawable.ic_star_fill);
                holder.starView1.setImageResource(R.drawable.ic_star_fill);
                holder.starView2.setImageResource(R.drawable.ic_star_fill);
                holder.starView3.setImageResource(R.drawable.ic_grade);
                holder.starView4.setImageResource(R.drawable.ic_grade);
                break;
            case 4:
                holder.starView0.setImageResource(R.drawable.ic_star_fill);
                holder.starView1.setImageResource(R.drawable.ic_star_fill);
                holder.starView2.setImageResource(R.drawable.ic_star_fill);
                holder.starView3.setImageResource(R.drawable.ic_star_fill);
                holder.starView4.setImageResource(R.drawable.ic_grade);
                break;
            case 5:
                holder.starView0.setImageResource(R.drawable.ic_star_fill);
                holder.starView1.setImageResource(R.drawable.ic_star_fill);
                holder.starView2.setImageResource(R.drawable.ic_star_fill);
                holder.starView3.setImageResource(R.drawable.ic_star_fill);
                holder.starView4.setImageResource(R.drawable.ic_star_fill);
        }

        return view;
    }

    static class ViewHolder {
        TextView nameText;
        TextView streetText;
        ImageView image;
        ImageView starView0;
        ImageView starView1;
        ImageView starView2;
        ImageView starView3;
        ImageView starView4;
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView,0);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
