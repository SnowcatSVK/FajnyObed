package com.snowcat.fajnyobed;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.snowcat.fajnyobed.Logic.DailyMenu;
import com.snowcat.fajnyobed.Logic.FoodGroup;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    private Restaurant restaurant;
    private DisplayImageOptions options;
    private ImageView promoPhoto;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private TextView restaurantNameTextView;
    private TextView restauratAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.restaurant_toolbar);
        setSupportActionBar(toolbar);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        promoPhoto = (ImageView) findViewById(R.id.restaurant_imageView);
        restaurantNameTextView = (TextView) findViewById(R.id.restaurant_name_textView);
        restauratAddressTextView = (TextView) findViewById(R.id.restaurant_address_textView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        getRestaurantDetails(intent.getStringExtra("restaurant_id"));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (restaurant.menus != null) {
            int i = 0;
            for (DailyMenu menu : restaurant.menus) {
                adapter.addFrag(new DailyMenuFragment(), menu.dateString, menu.groups);
                i++;
                if (i==3)
                    break;
            }
            viewPager.setAdapter(adapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
            tabLayout.setupWithViewPager(viewPager);
        }

    }

    public void getRestaurantDetails(final String restaurantId) {
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                jsonObject = MainActivity.handler.handleRequest("GetRestaurantDetail", restaurantId, null);
                Log.e("response", jsonObject.toString());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                restaurant = RestaurantFactory.restaurantDetailsFromJSON(jsonObject);
                ImageLoader.getInstance().displayImage(restaurant.promoPhotos.get(0), promoPhoto, options, animateFirstListener);
                ViewPager viewPager = (ViewPager) findViewById(R.id.restaurant_viewPager);
                setupViewPager(viewPager);
                restaurantNameTextView.setText(restaurant.name);
                restauratAddressTextView.setText(restaurant.street);
            }
        }.execute();
    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<DailyMenuFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        public void addFrag(DailyMenuFragment fragment, String title, ArrayList<FoodGroup> groups) {
            fragment.group = groups;
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
