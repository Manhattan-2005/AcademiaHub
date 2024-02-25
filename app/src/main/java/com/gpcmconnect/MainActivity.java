package com.gpcmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseFirestore db;
    private String username;

    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;

    @Override
    public void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
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

        if(savedInstanceState == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, new HomePage_Fragment());
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
    }

    public void logoutUser(MenuItem m) {
        try {
            Thread.sleep(500);
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        FirebaseAuth.getInstance().signOut();
        UserDetailsManager.getInstance().clearUserDetails();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }

}