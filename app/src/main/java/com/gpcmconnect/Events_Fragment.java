package com.gpcmconnect;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.gridlayout.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Events_Fragment extends Fragment {

    GridLayout adminNavigator;
    String designation;
    CardView add_event, view_events;

    @Override
    public void onStart() {
        super.onStart();
        UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
        designation = userDetailsManager.getDesignation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_events, container, false);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        view_events = view.findViewById(R.id.view_events);
        add_event = view.findViewById(R.id.add_event);
        adminNavigator = view.findViewById(R.id.event_grid_for_admin);
        UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
        designation = userDetailsManager.getDesignation();

        Log.d("designation", "designation: " + designation);

        if(designation!=null && designation.equals("admin")) {

            adminNavigator.setVisibility(View.VISIBLE);

        } else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new View_Events_Fragment()).addToBackStack(null);
            fragmentTransaction.commit();
        }

        view_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new View_Events_Fragment()).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Add_Event_Fragment()).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
