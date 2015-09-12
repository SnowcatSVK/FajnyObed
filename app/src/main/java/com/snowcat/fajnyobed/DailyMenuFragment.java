package com.snowcat.fajnyobed;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import com.snowcat.fajnyobed.Logic.FoodGroup;
import com.snowcat.fajnyobed.Logic.MenuListAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailyMenuFragment extends Fragment {

    public ArrayList<FoodGroup> group;
    public MenuListAdapter adapter;
    public ExpandableListView menuList;

    public DailyMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_daily_menu, container, false);
        menuList = (ExpandableListView) rootView.findViewById(R.id.restaurant_menu_list);
        adapter = new MenuListAdapter(getActivity(), group);
        menuList.setAdapter(adapter);
        for (int i = 0; i < group.size(); i++) {
            menuList.expandGroup(i);
        }
        menuList.setGroupIndicator(null);
        menuList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                menuList.expandGroup(groupPosition);
                return true;
            }
        });
        return rootView;
    }
}
