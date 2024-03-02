package com.gpcmconnect;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailsManager {

    FirebaseFirestore db;
    private static UserDetailsManager instance = null;
    private String email, name, designation;
    private String username;

    private ArrayList<String> names, emails, designations;

    private UserDetailsManager() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
        names = new ArrayList<>();
        emails = new ArrayList<>();
        designations = new ArrayList<>();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username = document.get("username").toString();
                        email = document.get("email").toString();
                        designation = document.get("designation").toString();
                        name = document.get("name").toString();
                    } else {
                        Log.d("fetch_exception", "No such document");
                    }
                } else {
                    Log.d("fetch_exception", "get failed with ", task.getException());
                }
            }
        });

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String userEmail = document.getString("email");
                                String userDesignation = document.getString("designation");
                                String userName = document.getString("name");

                                if(userName.equals("admin") || userName.equals("ETC")){
                                    continue;
                                }
                                names.add(userName);
                                emails.add(userEmail);
                                designations.add(userDesignation);

                            }
                        } else {
                            Log.d("fetch_exception", "get failed with ", task.getException());
                        }
                    }
                });

    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getDesignation() {
        return designation;
    }

    public static synchronized UserDetailsManager getInstance() {
        if (instance == null) {
            instance = new UserDetailsManager();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void clearUserDetails() {
        instance = null;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public ArrayList<String> getDesignations() {
        return designations;
    }

}


