package com.snowcat.fajnyobed;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;


import com.snowcat.fajnyobed.Logic.FoodGroup;
import com.snowcat.fajnyobed.Logic.MenuFactory;
import com.snowcat.fajnyobed.Logic.MenuListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    public MenuListAdapter adapter;
    public ExpandableListView menuList;
    private ProgressBar bar;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        menuList = (ExpandableListView) rootView.findViewById(R.id.restaurant_big_menu_list);
        bar = (ProgressBar) rootView.findViewById(R.id.big_menu_progressBar);
        return rootView;
    }

    public void getMenu(final String restaurantId) {
        new AsyncTask<Void, Void, Void>() {
            JSONObject jsonObject = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                bar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                jsonObject = MainActivity.handler.handleRequest("GetMenu", restaurantId, null);
                Log.e("response", jsonObject.toString());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                bar.setVisibility(View.INVISIBLE);
                try {
                    ArrayList<FoodGroup> groups = MenuFactory.menuFromJSON(jsonObject.optJSONArray("menu"));
                    adapter = new MenuListAdapter(getActivity(), groups);
                    menuList.setAdapter(adapter);
                    for (int i = 0; i < groups.size(); i++) {
                        menuList.expandGroup(i);
                    }
                    menuList.setGroupIndicator(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


}
