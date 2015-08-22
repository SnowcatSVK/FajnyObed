package com.snowcat.fajnyobed;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.snowcat.fajnyobed.Logic.City;
import com.snowcat.fajnyobed.Logic.CityAdapter;
import com.snowcat.fajnyobed.Logic.CityFactory;
import com.snowcat.fajnyobed.Logic.RestaurantAdapter;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;
import com.snowcat.fajnyobed.io.SecureDataClient;

import org.json.JSONObject;

import java.util.ArrayList;

public class CitiesActivity extends AppCompatActivity {
    public ArrayList<City> cities;
    public CityAdapter cityAdapter;
    public ListView cityListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        cityListView = (ListView) findViewById(R.id.cities_listView);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("ID", cities.get(position).id);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cities = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cities, menu);
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
        if (cities == null)
            new AsyncTask<Void, Void, Void>() {
                SecureDataClient client = new SecureDataClient();
                JSONObject request = new JSONObject();
                JSONObject jsonObject = null;

                @Override
                protected Void doInBackground(Void... params) {
                    String response = null;
                    try {
                        request.put("function", "GetCityList");
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
                    cities = CityFactory.fromJSON(jsonObject);
                    cityAdapter = new CityAdapter(CitiesActivity.this, cities);
                    cityListView.setAdapter(cityAdapter);
                }
            }.execute();
        else {
            cityAdapter = new CityAdapter(CitiesActivity.this, cities);
            cityListView.setAdapter(cityAdapter);
        }
    }
}
