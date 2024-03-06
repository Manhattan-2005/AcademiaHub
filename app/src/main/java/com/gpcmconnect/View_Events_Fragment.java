package com.gpcmconnect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class View_Events_Fragment extends Fragment {

    private ArrayAdapter<String> adapter;
    private ArrayList<String> completed, ongoing;
    TabLayout tabs;
    TabLayout.Tab ongoingTab;
    TabLayout.Tab completedTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_events, container, false);

        ListView listView = view.findViewById(R.id.listOfEvents);

        ongoing = new ArrayList<>();
        ongoing.add("Summer 2024");
        ongoing.add("Farewell");
        ongoing.add("Competition");

        completed = new ArrayList<>();
        completed.add("Unit Test 1");
        completed.add("Unit Test 2");
        completed.add("Freshers");
        completed.add("Competitions");

        tabs = view.findViewById(R.id.tabs);
        ongoingTab = tabs.getTabAt(0);
        completedTab = tabs.getTabAt(1);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ongoing);
        listView.setAdapter(adapter);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.equals(ongoingTab)) {
                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ongoing);
                } else if (tab.equals(completedTab)) {
                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, completed);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Optional: handle unselection events
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: handle reselection events
            }
        });

        return view;
    }
}