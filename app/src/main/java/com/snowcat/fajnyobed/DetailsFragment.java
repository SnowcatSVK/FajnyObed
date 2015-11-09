package com.snowcat.fajnyobed;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.snowcat.fajnyobed.Logic.OpenHoursAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    public ArrayList<TextView> textViews;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        textViews = new ArrayList<>();
        textViews.add((TextView) rootView.findViewById(R.id.open_hours_textView0));
        textViews.add((TextView) rootView.findViewById(R.id.open_hours_textView1));
        textViews.add((TextView) rootView.findViewById(R.id.open_hours_textView2));
        textViews.add((TextView) rootView.findViewById(R.id.open_hours_textView3));
        textViews.add((TextView) rootView.findViewById(R.id.open_hours_textView4));
        textViews.add((TextView) rootView.findViewById(R.id.open_hours_textView5));
        textViews.add((TextView) rootView.findViewById(R.id.open_hours_textView6));

        for (int i = 0; i < 7; i++) {
            textViews.get(i).setText(RestaurantActivity.restaurant.openHours.get(i));
        }
        return rootView;
    }


}
