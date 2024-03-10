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
            // Write data to the file
            writeDataToFile();
            // Open the file using implicit intent
            openFileWithImplicitIntent();
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

    private void writeDataToFile() {
        File directory = new File(getExternalFilesDir(null), DIRECTORY_NAME);
        File file = new File(directory, FILE_NAME);

        // Check if the file already exists
        if (file.exists()) {
            // File exists, no need to rewrite
            return;
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // External storage is available
            // Proceed with file operations
        } else {
            // External storage is not available
            Toast.makeText(this, "External storage not available", Toast.LENGTH_SHORT).show();
        }

        // Create the directory if it doesn't exist
        if (!directory.exists() && !directory.mkdirs()) {
            // If creating directories fails, show an error message and return
            Toast.makeText(this, "Error creating directories.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Write data to the file
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("Hello there!");
            writer.append("\nThis application was created as a micro-project for the subject of MAD");
            writer.append("\nWe are a team of two members Shivpratap Mithapalli and Pratik Salunke");
            writer.append("\nWe aim to achieve many great milestones and this is just the beginning.");
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data written to file: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ErrorFile", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(this, "Error writing data to file.", Toast.LENGTH_SHORT).show();
        }
    }


    private void openFileWithImplicitIntent() {
        File directory = new File(getExternalFilesDir(null), DIRECTORY_NAME);
        File file = new File(directory, FILE_NAME);

        if (file.exists()) {
            Uri fileUri = FileProvider.getUriForFile(this, "com.gpcmconnect.fileprovider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/plain");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } else {
            Toast.makeText(this, "File not found.", Toast.LENGTH_SHORT).show();
        }
    }


    private void openSocialMediaProfile(String profileUrl) {
        // Implement logic to open the social media profile in a browser or your preferred way
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

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack("home_page", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        } else {
            super.onBackPressed();
        }
    }
}