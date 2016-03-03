package com.snowcat.fajnyobed;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.snowcat.fajnyobed.Logic.City;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantAdapter;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;
import com.snowcat.fajnyobed.io.RequestHandler;
import com.snowcat.fajnyobed.io.SHA_256;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.Normalizer;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private ArrayList<Restaurant> restaurants;
    private ArrayAdapter<Restaurant> restaurantsAdapter;
    private ArrayList<Restaurant> searchResults;

    private ListView restaurantListView;
    public static ArrayList<City> cities;
    private int cityID = 0;
    private LocationManager lm;
    public static RequestHandler handler;
    ProgressBar progressBar;
    private boolean isSearchOn = false;
    private FavouritesFragment favouritesFragment;
    private boolean favouritesPresent = false;
    RelativeLayout layout;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        restaurantListView = (ListView) findViewById(R.id.restaurant_listView);
        restaurantListView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCache(new LimitedAgeDiskCache(cacheDir, 86400));
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        layout = (RelativeLayout) findViewById(R.id.layout_root_favourites);
        Intent extra = getIntent();
        cities = (ArrayList<City>) extra.getSerializableExtra("cities");
        cityID = extra.getIntExtra("ID", 1);
        for (City city : cities) {
            if (city.id == cityID) {
                cityName = city.name;
            }
        }
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
        String passwordHash = SHA_256.getHashString("VFZN!7y5yiu#2&c0WBgUFajnyObedofOqtA4W%HO1snf+TLtw");
        handler = new RequestHandler("http://api.fajnyobed.sk", passwordHash);
        progressBar = (ProgressBar) findViewById(R.id.restaurant_progressBar);
        searchEditText = (EditText) findViewById(R.id.search_editText);
        searchEditText.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.getId() == R.id.search_editText && !b) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                }
            }
        });
        restaurantListView.requestFocus();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (restaurants != null) {
                    search(editable, restaurantListView);

                    if (editable.length() >= 1) {
                        isSearchOn = true;
                    } else {
                        isSearchOn = false;
                        restaurantsAdapter = new RestaurantAdapter(MainActivity.this, restaurants);
                        restaurantsAdapter.notifyDataSetChanged();
                        restaurantListView.setAdapter(restaurantsAdapter);

                    }
                }
            }
        });


        restaurantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                String restaurantId;
                if (isSearchOn) {
                    restaurantId = String.valueOf(searchResults.get(position).id);
                } else {
                    restaurantId = String.valueOf(restaurants.get(position).id);
                }
                intent.putExtra("restaurant_id", restaurantId);
                startActivity(intent);
            }
        });
        favouritesFragment = new FavouritesFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.layout_root_favourites, favouritesFragment)
                .show(favouritesFragment)
                .hide(favouritesFragment)
                .commit();
        getRestaurants(cityID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_change_city:
                isSearchOn = false;
                searchEditText.setText("");
                Intent intent = new Intent(MainActivity.this, CitiesActivity.class);
                intent.putExtra("city_name", cityName);
                startActivityForResult(intent, 2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onMainFabClick(View v) {
        if (favouritesPresent) {
            animateChange(true);

        } else {
            animateChange(false);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        /*getSupportFragmentManager().beginTransaction()
                .hide(favouritesFragment)
                .commit();
        favouritesPresent = false;*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*{"function":"GetRestaurantDetail","restaurant_id":"4884"}*/
        /*if (cities == null)
            getCities();
            */
        searchEditText.clearFocus();
        restaurantListView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        favouritesFragment.getFavourites();
    }

    public void search(Editable charSequence, ListView listView) {
        searchResults = new ArrayList<>();
        String caseOne;
        String caseTwo;
        String sequence = Normalizer.normalize(charSequence, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").toLowerCase();
        for (Restaurant restaurant : restaurants) {
            caseOne = Normalizer.normalize(restaurant.name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "");
            caseTwo = Normalizer.normalize(restaurant.name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").toLowerCase();
            if ((caseOne.equalsIgnoreCase(sequence)) || (caseTwo.contains(sequence)))
                searchResults.add(restaurant);

        }
        restaurantsAdapter = new RestaurantAdapter(this, searchResults);
        restaurantsAdapter.notifyDataSetChanged();
        listView.setAdapter(restaurantsAdapter);
    }

    public void getRestaurants(final int id) {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject jsonObject = null;
            boolean noRestaurants = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    jsonObject = handler.handleRequest("GetRestaurantListByCity", String.valueOf(id), null);
                    if (jsonObject != null) {
                        Log.e("response", jsonObject.toString());
                        return true;
                    } else {
                        noRestaurants = true;
                        return false;
                    }
                } catch (SocketTimeoutException e) {
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid) {
                    restaurants = RestaurantFactory.fromJSON(jsonObject);
                    restaurantsAdapter = new RestaurantAdapter(MainActivity.this, restaurants);
                    restaurantListView.setAdapter(restaurantsAdapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (noRestaurants) {
                        Toast.makeText(MainActivity.this, "Pre toto mesto niesu dostupné reštaurácie s menu", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(MainActivity.this, "Zlyhalo internetové pripojenie", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                cityID = data.getIntExtra("ID", 1);
                for (City city : cities) {
                    if (city.id == cityID) {
                        cityName = city.name;
                        break;
                    }
                }
                if (restaurantsAdapter != null)
                    restaurantsAdapter.clear();
                getRestaurants(cityID);
            }
        } else {
            if (resultCode == RESULT_CANCELED) {
                getRestaurants(cityID);
            }
        }
    }

    public void animateChange(boolean hide) {
        if (hide) {
            Animation favAnimation = AnimationUtils.loadAnimation(this, R.anim.fly_right_from_center);
            layout.startAnimation(favAnimation);
            Animation listAnim = AnimationUtils.loadAnimation(this, R.anim.fly_right_from_start);
            restaurantListView.startAnimation(listAnim);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction()
                            .hide(favouritesFragment)
                            .commit();
                    favouritesPresent = false;
                }
            }, 300);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .show(favouritesFragment)
                    .commit();
            Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fly_left_from_end);
            layout.startAnimation(animation);
            Animation listAnim = AnimationUtils.loadAnimation(this, R.anim.fly_left_from_center);
            restaurantListView.startAnimation(listAnim);
            favouritesPresent = true;
            favouritesFragment.favouritesList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    String restaurantId = String.valueOf(favouritesFragment.favourites.get(groupPosition).id);
                    Intent i = new Intent(MainActivity.this, RestaurantActivity.class);
                    i.putExtra("restaurant_id", restaurantId);
                    startActivity(i);
                    animateChange(true);
                    favouritesPresent = false;
                    return true;
                }
            });
        }
    }
}
