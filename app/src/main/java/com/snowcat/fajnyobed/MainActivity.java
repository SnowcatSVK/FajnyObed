package com.snowcat.fajnyobed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantAdapter;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;
import com.snowcat.fajnyobed.io.SecureDataClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    TextView textView;
    private ArrayList<Restaurant> restaurants;
    private ArrayAdapter<Restaurant> restaurantsAdapter;
    private ListView restaurantListView;
    private int cityID = 0;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restaurantListView = (ListView) findViewById(R.id.restaurant_listView);
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*{"function":"GetRestaurantDetail","restaurant_id":"4884"}*/
        try {
            if (cityID == 0)
                initPosition();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getRestaurants(final int id) {
        new AsyncTask<Void, Void, Void>() {
            SecureDataClient client = new SecureDataClient();
            JSONObject request = new JSONObject();
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                String response = null;
                try {
                    request.put("function", "GetRestaurantListByCity");
                    request.put("city_id", "" + id);
                    response = client.createRetriever().execute(request);
                    jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                restaurants = RestaurantFactory.fromJSON(jsonObject);
                restaurantsAdapter = new RestaurantAdapter(MainActivity.this, restaurants);
                restaurantListView.setAdapter(restaurantsAdapter);
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
                if (addresses.size() > 0)
                    Log.e("Mesto", addresses.get(0).getLocality());
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
}
