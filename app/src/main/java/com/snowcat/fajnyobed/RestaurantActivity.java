package com.snowcat.fajnyobed;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    public static Restaurant restaurant;
    private DisplayImageOptions options;
    private ImageView promoPhoto;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private TextView restaurantNameTextView;
    private TextView restauratAddressTextView;
    private MenuFragment menuFragment;
    private DetailsFragment detailsFragment;
    private boolean fragmentPresent = false;
    private boolean menuSet = false;
    private boolean detailsPresent = false;
    private LinearLayout sadImageLayout;
    RelativeLayout layout;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.restaurant_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnLoading(getResources().getDrawable(R.drawable.boot_pic_red))
                .showImageOnFail(getResources().getDrawable(R.drawable.boot_pic_red))
                .build();
        promoPhoto = (ImageView) findViewById(R.id.restaurant_imageView);
        restaurantNameTextView = (TextView) findViewById(R.id.restaurant_name_textView);
        restauratAddressTextView = (TextView) findViewById(R.id.restaurant_address_textView);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;
        int height = size.y;
        layout = (RelativeLayout) findViewById(R.id.root_layout);
        ViewGroup.LayoutParams lp = promoPhoto.getLayoutParams();
        lp.height = (width / 16) * 9;
        lp.width = width;
        promoPhoto.setLayoutParams(lp);
        fab = (FloatingActionButton) findViewById(R.id.restaurant_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fragmentPresent) {
                    getSupportFragmentManager().beginTransaction()
                            .show(menuFragment).commit();
                    fragmentPresent = true;
                    Animation animation = AnimationUtils.loadAnimation(RestaurantActivity.this, R.anim.fade_in);
                    layout.startAnimation(animation);
                    if (!menuSet) {
                        menuFragment.getMenu(String.valueOf(restaurant.id));
                        menuSet = true;
                    }
                }
            }
        });
        Intent intent = getIntent();
        getRestaurantDetails(intent.getStringExtra("restaurant_id"));
        sadImageLayout = (LinearLayout) findViewById(R.id.sadface_imageLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_addFavourite:
                addFavourite();
                break;
            case R.id.action_info:
                if (!detailsPresent) {
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.slide_in, R.animator.slide_in)
                            .show(detailsFragment).commit();
                    detailsPresent = true;
                } else {
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.slide_out, R.animator.slide_out)
                            .hide(detailsFragment).commit();
                    detailsPresent = false;
                }
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (restaurant.menus != null) {
            sadImageLayout.setVisibility(View.GONE);
            for (DailyMenu menu : restaurant.menus) {
                adapter.addFrag(new DailyMenuFragment(), menu.dateString, menu.groups);
                ;
            }
            viewPager.setAdapter(adapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            if (restaurant.menus.size() > 3)
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(viewPager);
        } else {
            sadImageLayout.setVisibility(View.VISIBLE);
        }

    }

    public void getRestaurantDetails(final String restaurantId) {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject jsonObject = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    jsonObject = MainActivity.handler.handleRequest("GetRestaurantDetail", restaurantId, null);
                    if (jsonObject != null) {
                        Log.e("response", jsonObject.toString());
                        return true;
                    }
                } catch (SocketTimeoutException e) {
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid) {
                    restaurant = RestaurantFactory.restaurantDetailsFromJSON(jsonObject);
                    ImageLoader.getInstance().displayImage(restaurant.promoPhotos.get(2), promoPhoto, options, animateFirstListener);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.restaurant_viewPager);
                    setupViewPager(viewPager);
                    restaurantNameTextView.setText(restaurant.name);
                    restauratAddressTextView.setText(restaurant.street);
                    menuFragment = new MenuFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.root_layout, menuFragment)
                            .hide(menuFragment).commit();
                    detailsFragment = new DetailsFragment();
                    getFragmentManager().beginTransaction()
                            .add(R.id.restaurant_info_root, detailsFragment)
                            .hide(detailsFragment).commit();
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.clicker_layout);
                    linearLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(RestaurantActivity.this, "Zlyhalo internetové pripojenie", Toast.LENGTH_LONG).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },2000);
                }
            }
        }.execute();
    }

    public void addFavourite() {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject jsonObject = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    jsonObject = MainActivity.handler.handleRequest("AddFavoriteRestaurant", String.valueOf(restaurant.id),
                            Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    if (jsonObject != null) {
                        Log.e("response", jsonObject.toString());
                        return true;
                    }
                }
                catch (SocketTimeoutException e) {
                    return false;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid) {
                    try {
                        String text = jsonObject.getString("msg");
                        Toast.makeText(RestaurantActivity.this, text, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RestaurantActivity.this, "Zlyhalo pripojenie na internet", Toast.LENGTH_LONG).show();
                }
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

    @Override
    public void onBackPressed() {
        if (fragmentPresent) {
            Animation animation = AnimationUtils.loadAnimation(RestaurantActivity.this, R.anim.fade_out);
            layout.startAnimation(animation);
            fragmentPresent = false;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction()
                            .hide(menuFragment).commit();

                }
            }, 500);

        } else {
            if (detailsPresent) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_out, R.animator.slide_out)
                        .hide(detailsFragment).commit();
                detailsPresent = false;
            } else
                super.onBackPressed();
        }
    }
}