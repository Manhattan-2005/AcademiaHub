package com.gpcmconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class Users_Fragment extends Fragment implements UserDetailsManager.AllUserDetailsListener {

    ArrayList<String> names, designations, emails;
    ListView userList;
    User_List_Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
        names = userDetailsManager.getNames();
        emails = userDetailsManager.getEmails();
        designations = userDetailsManager.getDesignations();
        userDetailsManager.addAllUserDetailsListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_users, container, false);

        userList = view.findViewById(R.id.userList);
        updateUI();

        return view;
    }

    public void updateUI(){
        adapter = new User_List_Adapter(getContext(), names, emails, designations);
        userList.setAdapter(adapter);
    }

    @Override
    public void onAllUserDetailsUpdated() {
        names = UserDetailsManager.getInstance().getNames();
        emails = UserDetailsManager.getInstance().getEmails();
        designations = UserDetailsManager.getInstance().getDesignations();
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserDetailsManager.getInstance().removeAllUserDetailsListener(this);
    }

}