package com.gpcmconnect;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String DIRECTORY_NAME = "MyAppFiles";
    private static final String FILE_NAME = "team_details.txt";

    FirebaseUser user;
    String name, email;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    EventDetailsManager eventDetailsManager;
    UserDetailsManager userDetailsManager;

    @Override
    public void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();
        eventDetailsManager = EventDetailsManager.getInstance();
        userDetailsManager = UserDetailsManager.getInstance();
        name = userDetailsManager.getName();
        email = userDetailsManager.getEmail();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new HomePage_Fragment()).addToBackStack("home_page");
            transaction.commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Map<Integer, Fragment> fragmentMap = new HashMap<>();
                fragmentMap.put(R.id.bottom_home, new HomePage_Fragment());
                fragmentMap.put(R.id.bottom_events, new Events_Fragment());
                fragmentMap.put(R.id.bottom_users, new Users_Fragment());
                fragmentMap.put(R.id.bottom_profile, new Profile_Fragment());

                Fragment fragment = fragmentMap.get(item.getItemId());
                if (fragment != null) {
                    replaceFragment(fragment);
                }
                return true;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        addNameAndEmail();

        // Set up navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                handleNavigationItemSelected(item);
                return true;
            }
        });

    }

    public void addNameAndEmail() {
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        TextView textView1 = headerView.findViewById(R.id.name);
        TextView textView2 = headerView.findViewById(R.id.email);

        if (name != null) {
            textView1.setText(name);
            textView2.setText(email);
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        name = Objects.requireNonNull(document.get("name")).toString();
                        email = Objects.requireNonNull(document.get("email")).toString();
                        textView1.setText(name);
                        textView2.setText(email);
                    } else {
                        Log.d("fetch_exception", "No such document");
                    }
                } else {
                    Log.d("fetch_exception", "get failed with ", task.getException());
                }
            });
        }
    }

    private void handleNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            replaceFragment(new HomePage_Fragment());
        } else if (itemId == R.id.nav_about) {



        } else if (itemId == R.id.instagram) {
            openSocialMediaProfile("https://www.instagram.com/manhattan2005");
        } else if (itemId == R.id.linkedin) {
            openSocialMediaProfile("https://www.linkedin.com/in/shivpratap-mithapalli-2aa0a8257/");
        } else if (itemId == R.id.github) {
            openSocialMediaProfile("https://github.com/Manhattan-2005");
        } else if (itemId == R.id.nav_logout) {
            logoutUser(item);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void openSocialMediaProfile(String profileUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(profileUrl));
        startActivity(intent);
    }

    public void logoutUser(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        UserDetailsManager.getInstance().clearUserDetails();
        EventDetailsManager.getInstance().clearEventDetails();
        Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if ((fragmentManager.getBackStackEntryCount() > 0) && (bottomNavigationView.getSelectedItemId() != R.id.bottom_home)) {
            fragmentManager.popBackStack("home_page", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UserDetailsManager.getInstance().clearUserDetails();
        EventDetailsManager.getInstance().clearEventDetails();
    }

    @Override
    public void onStop() {
        super.onStop();
        UserDetailsManager.getInstance().clearUserDetails();
        EventDetailsManager.getInstance().clearEventDetails();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserDetailsManager.getInstance().clearUserDetails();
        EventDetailsManager.getInstance().clearEventDetails();
    }

}