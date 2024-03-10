package com.gpcmconnect;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class EventDetailsManager {

    FirebaseFirestore db;
    private final ArrayList<String> eventNames;
    private final ArrayList<String> eventDates;
    private final ArrayList<String> eventTimes;
    private final ArrayList<String> eventStatuses;
    private final ArrayList<String> eventCategories;
    private final ArrayList<String> eventDescriptions;
    private static EventDetailsManager instance = null;
    private static final List<EventDetailsManager.EventDetailsListener> eventDetailsListeners = new ArrayList<>();

    public interface EventDetailsListener {
        void onEventDetailsChanged();
    }

    private EventDetailsManager() {

        UserDetailsManager user = UserDetailsManager.getInstance();

        eventNames = new ArrayList<>();
        eventDates = new ArrayList<>();
        eventTimes = new ArrayList<>();
        eventStatuses = new ArrayList<>();
        eventCategories = new ArrayList<>();
        eventDescriptions = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    clearLists();

                    for (DocumentSnapshot document : task.getResult()) {
                        String eventName = document.getString("event_name");
                        String eventDate = document.getString("event_date");
                        String eventTime = document.getString("event_time");
                        String status = document.getString("status");
                        String category = document.getString("event_category");
                        String description = document.getString("event_description");

                        if(user.getDesignation() != null && category != null){
                            if(user.getDesignation().equals(category) || category.equals("All")){

                                addDataToLists(eventName, eventDate, eventTime, status, category, description);

                            }
                        }
                    }
                } else {
                    Log.d("fetch_exception", "get failed with ", task.getException());
                }
            }
        });

        //Adding a listener on details collection for receiving a real time update on data change
        db.collection("events").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.d("Fetch_Exception", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null) {

                //Clearing the Array Lists for rewriting of new data
                clearLists();

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                    String eventName = document.getString("event_name");
                    String eventDate = document.getString("event_date");
                    String eventTime = document.getString("event_time");
                    String status = document.getString("status");
                    String category = document.getString("event_category");
                    String description = document.getString("event_description");

                    if(user.getDesignation() != null && category != null){

                        if(category.equals("All") || (!category.equals("Student") && !user.getDesignation().equals("Student")) || (user.getDesignation().equals(category))) {

                            addDataToLists(eventName, eventDate, eventTime, status, category, description);

                        }

                    }

                }
                notifyEventDetailsListeners();

            } else {
                Log.d("Fetch_Exception", "Current data: null");
            }
        });
    }

    public void clearLists() {
        eventNames.clear();
        eventDates.clear();
        eventTimes.clear();
        eventStatuses.clear();
        eventCategories.clear();
        eventDescriptions.clear();
    }

    public void addDataToLists(String eventName, String eventDate, String eventTime, String status, String category, String description) {
        eventNames.add(eventName);
        eventDates.add(eventDate);
        eventTimes.add(eventTime);
        eventStatuses.add(status);
        eventCategories.add(category);
        eventDescriptions.add(description);
    }

    public static synchronized EventDetailsManager getInstance() {
        if (instance == null) {
            return new EventDetailsManager();
        } else {
            return instance;
        }
    }

    public ArrayList<String> getEventNames() {
        return eventNames;
    }

    public ArrayList<String> getEventDates() {
        return eventDates;
    }

    public ArrayList<String> getEventTimes() {
        return eventTimes;
    }

    public ArrayList<String> getEventStatuses() {
        return eventStatuses;
    }

    public ArrayList<String> getEventCategories() {
        return eventCategories;
    }

    public ArrayList<String> getEventDescriptions() {
        return eventDescriptions;
    }

    public void clearEventDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            instance = null;
    }

    public void addEventDetailsListener(EventDetailsListener eventDetailsListener) {
        eventDetailsListeners.add(eventDetailsListener);
    }

    public void notifyEventDetailsListeners() {
        for (EventDetailsListener listener : eventDetailsListeners) {
            listener.onEventDetailsChanged();
        }
    }

    public void removeEventDetailsListener(EventDetailsListener listener) {
        eventDetailsListeners.remove(listener);
    }

}
