package com.snowcat.fajnyobed;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.snowcat.fajnyobed.Logic.FavouritesArrayAdapter;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {

    public ListView favouritesList;
    public ArrayList<Restaurant> favourites;
    public FavouritesArrayAdapter adapter;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        favouritesList = (ListView) rootView.findViewById(R.id.favourites_listView);
        favouritesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteFavourite(position);
                return true;
            }
        });

        return rootView;
    }

    public void getFavourites() {
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    jsonObject = MainActivity.handler.handleRequest("GetFavoriteRestaurant", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID), null);
                    Log.e("response", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    int count = jsonObject.getInt("count");
                    if (count > 0) {
                        favourites = RestaurantFactory.parseFavourites(jsonObject.getJSONArray("list"));
                        adapter = new FavouritesArrayAdapter(getActivity(), favourites);
                        favouritesList.setAdapter(adapter);
                    } else {
                        favourites = null;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void deleteFavourite(final int position) {
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    jsonObject = MainActivity.handler.handleRequest("DeleteFavoriteRestaurant", String.valueOf(favourites.get(position).id),
                            Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
                    Log.e("response", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    String text = jsonObject.getString("msg");
                    Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                    favourites.remove(position);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
