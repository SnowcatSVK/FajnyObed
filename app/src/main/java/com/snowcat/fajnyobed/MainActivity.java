package com.snowcat.fajnyobed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.snowcat.fajnyobed.Logic.City;
import com.snowcat.fajnyobed.Logic.CityFactory;
import com.snowcat.fajnyobed.Logic.FavouritesCursorAdapter;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantAdapter;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;
import com.snowcat.fajnyobed.Database.FajnyObedContentProvider;
import com.snowcat.fajnyobed.Database.FavouritesTable;
import com.snowcat.fajnyobed.io.RequestHandler;
import com.snowcat.fajnyobed.io.SHA_256;

import org.json.JSONObject;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
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
    public static FavouritesCursorAdapter adapter;
    private boolean favouritesPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        restaurantListView = (ListView) findViewById(R.id.restaurant_listView);
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        cities = null;
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
        String passwordHash = SHA_256.getHashString("VFZN!7y5yiu#2&c0WBgUFajnyObedofOqtA4W%HO1snf+TLtw");
        handler = new RequestHandler("http://api.fajnyobed.sk", passwordHash);
        progressBar = (ProgressBar) findViewById(R.id.restaurant_progressBar);
        searchEditText = (EditText) findViewById(R.id.search_editText);

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
                search(charSequence, restaurantListView);
                if (charSequence.length() >= 1) {
                    isSearchOn = true;
                } else {
                    isSearchOn = false;
                    restaurantsAdapter = new RestaurantAdapter(MainActivity.this, restaurants);
                    restaurantsAdapter.notifyDataSetChanged();
                    restaurantListView.setAdapter(restaurantsAdapter);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchEditText.clearFocus();
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
        getLoaderManager().initLoader(0, null, this);
        adapter = new FavouritesCursorAdapter(this, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        favouritesFragment = new FavouritesFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.layout_root_favourites, favouritesFragment)
                .show(favouritesFragment)
                .hide(favouritesFragment)
                .commit();

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


        return super.onOptionsItemSelected(item);
    }

    public void onFabClick(View v) {
        if (favouritesPresent) {
            getSupportFragmentManager().beginTransaction()
                    .hide(favouritesFragment)
                    .commit();
            favouritesPresent = false;
        } else {
            getSupportFragmentManager().beginTransaction()
                    .show(favouritesFragment)
                    .commit();
            favouritesPresent = true;
            favouritesFragment.favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String restaurantId = MainActivity.adapter.getRestaurantId(position);
                    Intent i = new Intent(MainActivity.this, RestaurantActivity.class);
                    i.putExtra("restaurant_id", restaurantId);
                    startActivity(i);
                    getSupportFragmentManager().beginTransaction()
                            .hide(favouritesFragment)
                            .commit();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*{"function":"GetRestaurantDetail","restaurant_id":"4884"}*/
        if (cities == null)
            getCities();
        restaurantListView.requestFocus();

    }

    public void search(CharSequence charSequence, ListView listView) {
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
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    jsonObject = handler.handleRequest("GetRestaurantListByCity", String.valueOf(id), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("response", jsonObject.toString());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                restaurants = RestaurantFactory.fromJSON(jsonObject);
                restaurantsAdapter = new RestaurantAdapter(MainActivity.this, restaurants);
                restaurantListView.setAdapter(restaurantsAdapter);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }

    public void getCities() {
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    jsonObject = handler.handleRequest("GetCityList", null, null);
                    Log.e("response", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                cities = CityFactory.fromJSON(jsonObject);
                try {
                    if (cityID == 0)
                        initPosition();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void initPosition() throws IOException {
        if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Poloha");
            builder.setMessage("Pre načítanie reštaurácií podľa polohy povolte lokalizačné služby");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 1);
                }
            });
            builder.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, CitiesActivity.class);
                    startActivityForResult(intent, 2);
                    dialogInterface.cancel();
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        } else {
            double[] gps = getGPS();
            if (gps[0] != 0.0 && gps[1] != 0.0) {
                Log.e("Poloha", "" + gps[0] + ";" + gps[1]);
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(gps[0], gps[1], 1);
                if (addresses.size() > 0) {
                    String mesto = addresses.get(0).getLocality();
                    Log.e("Mesto", mesto);
                    for (City city : cities) {
                        if (city.name.equalsIgnoreCase(mesto)) {
                            cityID = city.id;
                            getRestaurants(cityID);
                        }
                    }
                }
            } else {
                Log.e("MapInit", "nieto GPS");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Poloha");
                builder.setMessage("Poloha nedostupná");
                builder.setPositiveButton("Vybrať mesto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Show location settings when the user acknowledges the alert dialog
                        Intent intent = new Intent(MainActivity.this, CitiesActivity.class);
                        startActivityForResult(intent, 2);
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                Dialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

            }
        }
    }

    private double[] getGPS() {
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }

        double[] gps = new double[2];
        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        Log.e("coordinates", "" + gps[0] + "," + gps[1]);
        return gps;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                cityID = data.getIntExtra("ID", 1);
                getRestaurants(cityID);
            }
        } else {
            if (resultCode == RESULT_CANCELED) {
                getRestaurants(1);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projectionHistory = new String[]{FavouritesTable.COLUMN_ID, FavouritesTable.COLUMN_RESTAURANT_NAME,
                FavouritesTable.COLUMN_RESTAURANT_PLACE, FavouritesTable.COLUMN_RESTAURANT_ID
        };
        return new CursorLoader(this, FajnyObedContentProvider.CONTENT_URI_FAVOURITE,
                projectionHistory, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
