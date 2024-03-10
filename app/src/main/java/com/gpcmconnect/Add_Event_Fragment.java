package com.gpcmconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Add_Event_Fragment extends Fragment {

    FirebaseFirestore db;
    Button addEventButton;
    EditText name, time, date, description;
    ProgressBar progressBar;
    Calendar calendar;
    Spinner categorySpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        name = view.findViewById(R.id.eventName);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        categorySpinner = view.findViewById(R.id.category);
        description = view.findViewById(R.id.description);
        progressBar = view.findViewById(R.id.progressBar);
        addEventButton = view.findViewById(R.id.addEventButton);

        String[] categoryOptions = {"Select Category", "Students", "Faculties", "HOD", "All"};

        // Create ArrayAdapter for the Spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryOptions);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                date.setText(dateFormat.format(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        requireContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                time.setText(timeFormat.format(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                );
                timePickerDialog.show();
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addEventButton.setVisibility(View.GONE);

                // Check if the event with the given name already exists
                checkEventExists(name.getText().toString(), date.getText().toString(), time.getText().toString(), categorySpinner.getSelectedItem().toString(), description.getText().toString());

            }
        });

        return view;
    }

    private void checkEventExists(String eventName, String date, String time, String category, String description) {

        CollectionReference eventsRef = db.collection("events");
        DocumentReference eventRef = eventsRef.document(eventName);

        eventRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Event already exists
                        Toast.makeText(getContext(), "Event Already Exists", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        addEventButton.setVisibility(View.VISIBLE);
                    } else {
                        // Event does not exist, proceed to add
                        addEvent(eventName, date, time, category, description);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error occurred while checking existence
                    Toast.makeText(getContext(), "Error Checking Event Existence", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    addEventButton.setVisibility(View.VISIBLE);
                });
    }

    private void addEvent(String eventName, String date, String time, String category, String description) {

        if (eventName.isEmpty() || date.isEmpty() || time.isEmpty() || category.equals("Select Category") || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields and select a category", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            addEventButton.setVisibility(View.VISIBLE);
            return; // Exit the method if validation fails
        }

        // Reference to the ongoing_events collection
        CollectionReference eventsRef = db.collection("events");

        // Reference to the specific event document using the event name as the document ID
        DocumentReference eventRef = eventsRef.document(eventName);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("event_name", eventName);
        eventData.put("event_date", date);
        eventData.put("event_time", time);
        eventData.put("status", "on_going");
        eventData.put("event_category", category);
        eventData.put("event_description", description);

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

    private void redirectToViewFragment() {
        // Implement your redirection logic here after a successful operation
        // You might want to use a Handler to delay the redirection

        // Example: Replace the current fragment with View_Fragment
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new View_Events_Fragment())
                .addToBackStack(null)
                .commit();
    }
}
