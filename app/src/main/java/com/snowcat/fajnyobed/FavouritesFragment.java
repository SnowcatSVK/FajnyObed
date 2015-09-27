package com.snowcat.fajnyobed;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {


    public FavouritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        ListView favouritesList = (ListView) rootView.findViewById(R.id.favourites_listView);
        favouritesList.setAdapter(MainActivity.adapter);
        favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String restaurantId = MainActivity.adapter.getRestaurantId(position);
                Intent i = new Intent(getActivity(), RestaurantActivity.class);
                i.putExtra("restaurant_id", restaurantId);
                startActivity(i);
            }
        });
        return rootView;
    }


}
