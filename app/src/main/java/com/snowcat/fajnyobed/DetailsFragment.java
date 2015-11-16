package com.snowcat.fajnyobed;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
        if (RestaurantActivity.restaurant.paymentCards.size()==0) {
            TextView tv0 = (TextView) rootView.findViewById(R.id.payments_textView);
            TextView tv1 = (TextView) rootView.findViewById(R.id.payments_textView1);
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.paycards_linearLayout);
            tv0.setVisibility(View.GONE);
            tv1.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
        if (RestaurantActivity.restaurant.foodTickets.size()==0) {
            TextView tv0 = (TextView) rootView.findViewById(R.id.food_tickets_textView);
            TextView tv1 = (TextView) rootView.findViewById(R.id.food_tickets_textView1);
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.food_tickets_linearLayout);
            tv0.setVisibility(View.GONE);
            tv1.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
        return rootView;
    }


}
