package com.gpcmconnect;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class Add_Event_Fragment extends Fragment {

    FirebaseFirestore db;
    Button addEventButton;
    EditText name, time, date;
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        name = view.findViewById(R.id.eventName);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        progressBar = view.findViewById(R.id.progressBar);
        addEventButton = view.findViewById(R.id.addEventButton);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addEventButton.setVisibility(View.GONE);

                checkEventExists(name.getText().toString(), date.getText().toString(), time.getText().toString());

                date.setText("");
                time.setText("");
                name.setText("");
            }
        });

        return view;
    }

    private void addEvent(String eventName, String date, String time) {
        // Reference to the ongoing_events collection
        DocumentReference eventsRef = db.collection("events").document("ongoing_events");

        CollectionReference eventDetailsRef = eventsRef.collection("eventDetails");

        // Reference to the specific event document using the event name as the document ID
        DocumentReference eventRef = eventDetailsRef.document(eventName);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("event_name", eventName);
        eventData.put("event_date", date);
        eventData.put("event_time", time);

        eventRef.set(eventData)
                .addOnSuccessListener(aVoid -> {
                    // Event added successfully
                    Toast.makeText(getContext(), "Event Added Successfully", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    progressBar.setVisibility(View.GONE);
                    addEventButton.setVisibility(View.VISIBLE);

                    redirectToViewFragment();
                })
                .addOnFailureListener(e -> {
                    // Failed to add event
                    Toast.makeText(getContext(), "Event Addition Failed", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    addEventButton.setVisibility(View.VISIBLE);
                });
    }

    private void checkEventExists(String eventName, String date, String time) {
        // Reference to the ongoing_events collection
        DocumentReference eventsRef = db.collection("events").document("ongoing_events");

        CollectionReference eventDetailsRef = eventsRef.collection("eventDetails");

        // Reference to the specific event document using the event name as the document ID
        DocumentReference eventRef = eventDetailsRef.document(eventName);

        // Check if the event with the given name already exists
        eventRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Event already exists
                        Toast.makeText(getContext(), "Event Already Exists", Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                        addEventButton.setVisibility(View.VISIBLE);
                    } else {
                        // Event does not exist, proceed to add
                        addEvent(eventName, date, time);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while checking existence
                    Toast.makeText(getContext(), "Error Checking Event Existence", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    addEventButton.setVisibility(View.VISIBLE);
                });
    }

    private void redirectToViewFragment() {
        // Implement your redirection logic here after a successful operation
        // You might want to use a Handler to delay the redirection

        // Example: Replace the current fragment with View_Fragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new View_Events_Fragment())
                .addToBackStack(null)
                .commit();
    }
}
