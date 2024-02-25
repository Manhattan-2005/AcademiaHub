package com.gpcmconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    String username;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
        username = userDetailsManager.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        TextView greet = view.findViewById(R.id.username);

        if(username == null) {
            UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
            username = userDetailsManager.getUsername();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            if(username != null) {
                greet.setText("Hello, " + username);
            } else {
                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                username = document.get("username").toString();
                                greet.setText("Hello, " + username);
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
            Intent intent  = new Intent(getContext(), Login.class);
            startActivity(intent);
            onStop();
            onDestroy();
        }

        return view;
    }
}