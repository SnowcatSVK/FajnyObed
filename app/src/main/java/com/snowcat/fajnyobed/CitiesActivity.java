package com.snowcat.fajnyobed;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.snowcat.fajnyobed.Logic.City;
import com.snowcat.fajnyobed.Logic.CityAdapter;
import com.snowcat.fajnyobed.Logic.CityFactory;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantAdapter;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;
import com.snowcat.fajnyobed.io.SecureDataClient;

import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;

public class CitiesActivity extends AppCompatActivity {
    public ArrayList<City> cities;
    public ArrayList<City> searchResults;
    public CityAdapter cityAdapter;
    public ListView cityListView;
    private EditText searchEditText;
    private boolean isSearchOn;
    private boolean fromSplash;
    private TextView cityTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.city_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Intent extra = getIntent();
        fromSplash = extra.getBooleanExtra("from_splash", false);
        cityListView = (ListView) findViewById(R.id.cities_listView);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!fromSplash) {
                    Intent intent = new Intent();
                    if (isSearchOn)
                        intent.putExtra("ID", searchResults.get(position).id);
                    else
                        intent.putExtra("ID", cities.get(position).id);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent(CitiesActivity.this, MainActivity.class);
                    intent.putExtra("cities", cities);
                    if (isSearchOn)
                        intent.putExtra("ID", searchResults.get(position).id);
                    else
                        intent.putExtra("ID", cities.get(position).id);
                    if (extra.hasExtra("favourites"))
                        intent.putExtra("favourites", extra.getSerializableExtra("favourites"));
                    startActivity(intent);
                    finish();
                }
            }
        });
        cityTextView = (TextView) findViewById(R.id.city_textView);
        cities = (ArrayList<City>) extra.getSerializableExtra("cities");
        if (fromSplash) {
            cityTextView.setText("Vyberte mesto");
        } else {
            cityTextView.setText(extra.getStringExtra("city_name"));
        }
        searchEditText = (EditText) findViewById(R.id.city_search_editText);

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.getId() == R.id.search_editText && !b) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                }
            }
        });
        cityListView.requestFocus();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                search(charSequence, cityListView);
                if (charSequence.length() >= 1) {
                    isSearchOn = true;
                } else {
                    isSearchOn = false;
                    cityAdapter = new CityAdapter(CitiesActivity.this, cities);
                    cityAdapter.notifyDataSetChanged();
                    cityListView.setAdapter(cityAdapter);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchEditText.clearFocus();
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

        return super.onOptionsItemSelected(item);
    }

    public void search(CharSequence charSequence, ListView listView) {
        searchResults = new ArrayList<>();
        String caseOne;
        String caseTwo;
        String sequence = Normalizer.normalize(charSequence, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").toLowerCase();
        for (City city : cities) {
            caseOne = Normalizer.normalize(city.name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "");
            caseTwo = Normalizer.normalize(city.name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\s+", "").toLowerCase();
            if ((caseOne.equalsIgnoreCase(sequence)) || (caseTwo.contains(sequence)))
                searchResults.add(city);

        }
        cityAdapter = new CityAdapter(this, searchResults);
        cityAdapter.notifyDataSetChanged();
        listView.setAdapter(cityAdapter);
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

        //searchEditText.clearFocus();
        cityListView.requestFocus();
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }
}
