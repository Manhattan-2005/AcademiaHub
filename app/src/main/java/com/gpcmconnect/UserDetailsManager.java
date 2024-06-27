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
import java.util.List;
import java.util.Objects;

public class UserDetailsManager {

    private static List<AllUserDetailsListener> userDetailsListeners = new ArrayList<>();

    public interface AllUserDetailsListener {
        void onAllUserDetailsUpdated();
    }

    FirebaseFirestore db;
    private static UserDetailsManager instance = null;
    private String name, designation, email;
    private String username;

    private ArrayList<String> names, emails, designations;

    private UserDetailsManager() {

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        names = new ArrayList<>();
        emails = new ArrayList<>();
        designations = new ArrayList<>();

        if(user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid());

            //Retrieving the details of the current user
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            username = Objects.requireNonNull(document.get("username")).toString();
                            designation = Objects.requireNonNull(document.get("designation")).toString();
                            name = Objects.requireNonNull(document.get("name")).toString();
                            email = Objects.requireNonNull(document.get("email")).toString();
                        } else {
                            Log.d("fetch_exception", "No such document");
                        }
                    } else {
                        Log.d("fetch_exception", "get failed with ", task.getException());
                    }
                }
            });
        }

        //Retrieving the details of all the users for Users_Fragment
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    names.clear();
                    emails.clear();
                    designations.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        String userEmail = document.getString("email");
                        String userDesignation = document.getString("designation");
                        String userName = document.getString("name");

                        if(userName != null){
                            if (userName.equals("admin")) {
                                continue;
                            }
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

        //Adding a listener on users collection for receiving a real time update on data change
        db.collection("users").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.d("Fetch_Exception", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null) {

                //Clearing the Array Lists for rewriting of new data
                names.clear();
                emails.clear();
                designations.clear();

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String userEmail = document.getString("email");
                    String userDesignation = document.getString("designation");
                    String userName = document.getString("name");

                    if  (userName != null && (userName.equals("admin") || userName.equals("ETC"))) {
                        continue;
                    }

                    names.add(userName);
                    emails.add(userEmail);
                    designations.add(userDesignation);
                }

                notifyAllUserDetailsListeners();
            } else {
                Log.d("Fetch_Exception", "Current data: null");
            }
        });

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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

    public void addAllUserDetailsListener(AllUserDetailsListener listener) {
        userDetailsListeners.add(listener);
    }

    public void removeAllUserDetailsListener(AllUserDetailsListener listener) {
        userDetailsListeners.remove(listener);
    }

    private void notifyAllUserDetailsListeners() {
        for (AllUserDetailsListener listener : userDetailsListeners) {
            listener.onAllUserDetailsUpdated();
        }
    }

}


