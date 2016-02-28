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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.snowcat.fajnyobed.Logic.FavouriteRestaurant;
import com.snowcat.fajnyobed.Logic.FavouritesArrayAdapter;
import com.snowcat.fajnyobed.Logic.Restaurant;
import com.snowcat.fajnyobed.Logic.RestaurantFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {

    public ExpandableListView favouritesList;
    public ArrayList<FavouriteRestaurant> favourites;
    public FavouritesArrayAdapter adapter;
    public TextView noFavTextView;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        favouritesList = (ExpandableListView) rootView.findViewById(R.id.favourites_listView);
        favouritesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    //do your per-item callback here
                    return false; //true if we consumed the click, false if not

                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    //do your per-group callback here
                    Log.e("Position",String.valueOf(position));
                    for (int i = 0; i < favourites.size(); i++) {
                        FavouriteRestaurant restaurant = favourites.get(i);
                        Log.e("Position in list",String.valueOf(restaurant.positionInList));
                        if (restaurant.positionInList == position)
                            deleteFavourite(i);
                    }

                    return true; //true if we consumed the click, false if not

                } else {
                    // null item; we don't consume the click
                    return false;
                }
            }
        });
        noFavTextView = (TextView) rootView.findViewById(R.id.no_fav_textView);
        return rootView;
    }

    public void getFavourites() {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject jsonObject = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    jsonObject = MainActivity.handler.handleRequest("GetFavoriteRestaurant", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID), null);
                    if (jsonObject != null) {
                        Log.e("response", jsonObject.toString());
                        return true;
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid) {
                    try {
                        int count = jsonObject.getInt("count");
                        if (count > 0) {
                            favourites = RestaurantFactory.parseFavourites(jsonObject.getJSONArray("list"));
                            adapter = new FavouritesArrayAdapter(getActivity(), favourites);
                            favouritesList.setAdapter(adapter);
                            noFavTextView.setVisibility(View.GONE);
                            favouritesList.setGroupIndicator(null);
                            for (int i = 0; i < favourites.size(); i++) {
                                favouritesList.expandGroup(i);
                                if (i == 0) {
                                    favourites.get(i).positionInList = 0;
                                }
                                if (i + 1 < favourites.size()) {
                                    favourites.get(i + 1).positionInList = favourites.get(i).positionInList + favouritesList.getExpandableListAdapter().getChildrenCount(i) + 1;
                                }

                            }
                        } else {
                            favourites = null;
                            noFavTextView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    public void deleteFavourite(final int position) {
        new AsyncTask<Void, Void, Boolean>() {
            JSONObject jsonObject = null;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    jsonObject = MainActivity.handler.handleRequest("DeleteFavoriteRestaurant", String.valueOf(favourites.get(position).id),
                            Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
                    if (jsonObject != null) {
                        Log.e("response", jsonObject.toString());
                        return true;
                    } else {
                        return false;
                    }
                } catch (SocketTimeoutException e) {
                    Toast.makeText(getActivity(), "Server neodpovedá", Toast.LENGTH_LONG).show();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Zlyhalo internetové pripojenie", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                super.onPostExecute(aVoid);
                try {
                    String text = jsonObject.getString("msg");
                    Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                    favourites.remove(position);
                    favouritesList.invalidateViews();
                    adapter.notifyDataSetChanged();
                    for (int i = 0; i < favourites.size(); i++) {
                        if (i == 0) {
                            favourites.get(i).positionInList = 0;
                        }
                        if (i + 1 < favourites.size()) {
                            favourites.get(i + 1).positionInList = favourites.get(i).positionInList + favouritesList.getExpandableListAdapter().getChildrenCount(i) + 1;
                        }

                    }
                    if (favourites.size() == 0)
                        noFavTextView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
