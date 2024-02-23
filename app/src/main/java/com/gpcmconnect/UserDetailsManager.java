package com.gpcmconnect;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserDetailsManager {

    FirebaseUser user;
    FirebaseFirestore db;

    private static UserDetailsManager instance = null;
    private String email, name, designation;
    private String username;
    private Image profile;

    private UserDetailsManager() {

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username = Objects.requireNonNull(document.get("username")).toString();
                    } else {
                        Log.d("fetch_exception", "No such document");
                    }
                } else {
                    Log.d("fetch_exception", "get failed with ", task.getException());
                }
            }
        });

    }

    public interface UsernameListener {
        void onUsernameRetrieved(String username);
    }

    public static synchronized UserDetailsManager getInstance() {
        if (instance == null) {
            instance = new UserDetailsManager();
        }
        return instance;
    }

    public String getUsername() {
        if(username != null) {
            return username;
        } else {
            return "User";
        }
    }

    public Map<String, Object> getUserDetails() {
        HashMap<String, Object> userDetails = new HashMap<String, Object>();
        userDetails.put("name", name);
        userDetails.put("email", email);
        userDetails.put("designation", designation);
        userDetails.put("username", username);
        userDetails.put("profile", profile);

        return userDetails;
    }

}
