package com.snowcat.fajnyobed;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.snowcat.fajnyobed.Logic.OpenHoursAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    public ListView openHoursListView;
    public OpenHoursAdapter adapter;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        openHoursListView = (ListView) rootView.findViewById(R.id.open_hours_listView);
        adapter = new OpenHoursAdapter(getActivity(),RestaurantActivity.restaurant.openHours);
        openHoursListView.setAdapter(adapter);
        return rootView;
    }


}
