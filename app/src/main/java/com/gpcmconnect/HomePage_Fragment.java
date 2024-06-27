package com.gpcmconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomePage_Fragment extends Fragment {

    FirebaseUser user;
    FirebaseFirestore db;
    String username, designation;
    CardView add_event, view_events, view_users, view_profile, view_syllabus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get database instance
        db = FirebaseFirestore.getInstance();

        //set the username from the UserDetailsManager
        UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
        username = userDetailsManager.getUsername();
        designation = userDetailsManager.getDesignation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        TextView greet = view.findViewById(R.id.username);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        //initialise the CardViews
        view_events = view.findViewById(R.id.view_events);
        add_event = view.findViewById(R.id.add_event); //only for admin
        view_syllabus = view.findViewById(R.id.view_syllabus);
        view_users = view.findViewById(R.id.view_users);
        view_profile = view.findViewById(R.id.view_profile);

        if(designation!=null && designation.equals("admin")) {
            add_event.setVisibility(View.VISIBLE);
            view_syllabus.setVisibility(View.GONE);
        }

        //set username if not already set
        if(username == null) {
            UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
            username = userDetailsManager.getUsername();
        }

        //check if the user is logged in or not (to avoid further exceptions on null object)
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            if(username != null) {

                //set the username to the textview
                greet.setText(String.format("Hello, %s", username));

            } else {

                //else retrieve username from the FireStore (db)
                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                username = Objects.requireNonNull(document.get("username")).toString();
                                greet.setText(String.format("Hello, %s", username));
                            } else {
                                Log.d("fetch_exception", "No such document");
                            }
                        } else {
                            Log.d("fetch_exception", "get failed with ", task.getException());
                        }
                    }
                });
            }
        } else {

            //if user is not logged in, redirect him to login page
            Intent intent  = new Intent(getContext(), Login.class);
            startActivity(intent);
            onStop();
            onDestroy();

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

        view_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Users_Fragment()).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Profile_Fragment()).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        view_syllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://msbte.org.in/portal/curriculum-search/"));
                startActivity(intent);
                onStop();
                onDestroy();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        UserDetailsManager.getInstance().clearUserDetails();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserDetailsManager.getInstance().clearUserDetails();
    }

}