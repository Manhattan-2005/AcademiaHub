package com.gpcmconnect;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Objects;

public class Profile_Fragment extends Fragment {
    String username, name, email, designation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();
        username = userDetailsManager.getUsername();
        name = userDetailsManager.getName();
        email = userDetailsManager.getEmail();
        designation = userDetailsManager.getDesignation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        TextView nameView = view.findViewById(R.id.name);
        TextView usernameView = view.findViewById(R.id.username);
        TextView emailView = view.findViewById(R.id.email);
        TextView designationView = view.findViewById(R.id.designation);
        Button logout = view.findViewById(R.id.logout);

        if(name != null){

            nameView.setText(name);
            usernameView.setText(username);
            emailView.setText(email);
            designationView.setText(designation);

        } else {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            //else retrieve username from the FireStore (db)
            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            name = Objects.requireNonNull(document.get("name")).toString();
                            email = Objects.requireNonNull(document.get("email")).toString();
                            username = Objects.requireNonNull(document.get("username")).toString();
                            designation = Objects.requireNonNull(document.get("designation")).toString();

                            nameView.setText(name);
                            usernameView.setText(username);
                            emailView.setText(email);
                            designationView.setText(designation);

                        } else {
                            Log.d("fetch_exception", "No such document");
                        }
                    } else {
                        Log.d("fetch_exception", "get failed with ", task.getException());
                    }
                }
            });
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                UserDetailsManager.getInstance().clearUserDetails();
                EventDetailsManager.getInstance().clearEventDetails();
                Toast.makeText(getContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                onDestroy();
            }
        });

        return view;
    }
}