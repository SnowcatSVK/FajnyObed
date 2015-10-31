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
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import com.snowcat.fajnyobed.Logic.City;
import com.snowcat.fajnyobed.Logic.CityFactory;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;
import com.snowcat.fajnyobed.io.RequestHandler;
import com.snowcat.fajnyobed.io.SHA_256;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SplashScreenActivity extends Activity {

    private LocationManager lm;
    public ArrayList<City> cities;
    public ArrayList<Restaurant> favourites;
    private int cityID = 0;
    public RequestHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        String passwordHash = SHA_256.getHashString("VFZN!7y5yiu#2&c0WBgUFajnyObedofOqtA4W%HO1snf+TLtw");
        handler = new RequestHandler("http://api.fajnyobed.sk", passwordHash);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        favourites = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*final Intent intent = new Intent(this, MainActivity.class);

        */
        getCities();
    }

    public void initPosition() throws IOException {
        if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            final Intent intent = new Intent(SplashScreenActivity.this, CitiesActivity.class);
            intent.putExtra("cities", cities);
            intent.putExtra("from_splash", true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 2500);
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
                            final Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            intent.putExtra("ID", cityID);
                            intent.putExtra("cities", cities);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1500);
                            return;
                        }
                    }
                    final Intent intent = new Intent(SplashScreenActivity.this, CitiesActivity.class);
                    intent.putExtra("cities", cities);
                    intent.putExtra("from_splash", true);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                            finish();
                        }
                    }, 1500);
                }
            } else {
                Log.e("MapInit", "nieto GPS");
                final Intent intent = new Intent(SplashScreenActivity.this, CitiesActivity.class);
                intent.putExtra("cities", cities);
                intent.putExtra("from_splash", true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        finish();
                    }
                }, 1500);
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
                    initPosition();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
//Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)

