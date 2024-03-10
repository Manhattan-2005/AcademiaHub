package com.gpcmconnect;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class View_Events_Fragment extends Fragment implements EventDetailsManager.EventDetailsListener, Event_List_Adapter.OnDeleteClickListener {

    Event_List_Adapter adapter1, adapter2;
    private ArrayList<String> eventNames, eventDates, eventTimes, eventStatuses, eventCategories, eventDescriptions;
    private ArrayList<String> ongoingEventNames, ongoingEventDates, ongoingEventTimes, ongoingEventStatuses, ongoingEventCategories, ongoingEventDescriptions;
    private ArrayList<String> completedEventNames, completedEventDates, completedEventTimes, completedEventStatuses, completedEventCategories, completedEventDescriptions;
    TabLayout tabs;
    TabLayout.Tab ongoingTab;
    TabLayout.Tab completedTab;
    ListView listView1, listView2;
    EventDetailsManager eventDetailsManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDetailsManager = EventDetailsManager.getInstance();
        eventDetailsManager.addEventDetailsListener(this);
        eventNames = eventDetailsManager.getEventNames();
        eventDates = eventDetailsManager.getEventDates();
        eventTimes = eventDetailsManager.getEventTimes();
        eventStatuses = eventDetailsManager.getEventStatuses();
        eventCategories = eventDetailsManager.getEventCategories();
        eventDescriptions = eventDetailsManager.getEventDescriptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_events, container, false);

        listView1 = view.findViewById(R.id.listOfEvents1);
        listView2 = view.findViewById(R.id.listOfEvents2);
        tabs = view.findViewById(R.id.tabs);
        ongoingTab = tabs.getTabAt(0);
        completedTab = tabs.getTabAt(1);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Manually select the ongoing tab
        tabs.selectTab(ongoingTab);

        // Call the method to create adapters
        createAdapters();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                handleTabSelection(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Handle the refresh action, e.g., fetch updated data
                refreshData();
            }
        });

        return view;
    }

    private void refreshData() {
        if (!isRefreshing) {
            isRefreshing = true;
            swipeRefreshLayout.setRefreshing(true);

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Refresh the data in the existing fragment
                    if (isAdded()) {
                        createAdapters();
                        isRefreshing = false;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        }
    }

    private void handleTabSelection(TabLayout.Tab tab) {
        if (tab.equals(ongoingTab)) {
            listView1.setVisibility(View.VISIBLE);
            listView2.setVisibility(View.GONE);
            if (adapter1 != null) {
                adapter1.setTab("on_going");
                adapter1.notifyDataSetChanged();
            }
        } else if (tab.equals(completedTab)) {
            listView1.setVisibility(View.GONE);
            listView2.setVisibility(View.VISIBLE);
            if (adapter2 != null) {
                adapter2.setTab("completed");
                adapter2.notifyDataSetChanged();
            }
        }
    }

    private void createAdapters() {
        // Create adapters only if the data is available
        if (eventNames != null && eventDates != null && eventTimes != null && eventStatuses != null &&
                !eventNames.isEmpty() && !eventDates.isEmpty() && !eventTimes.isEmpty() && !eventStatuses.isEmpty()) {

            // Initialize the ArrayLists here
            ongoingEventNames = new ArrayList<>();
            ongoingEventDates = new ArrayList<>();
            ongoingEventTimes = new ArrayList<>();
            ongoingEventStatuses = new ArrayList<>();
            ongoingEventCategories = new ArrayList<>();
            ongoingEventDescriptions = new ArrayList<>();

            completedEventNames = new ArrayList<>();
            completedEventDates = new ArrayList<>();
            completedEventTimes = new ArrayList<>();
            completedEventStatuses = new ArrayList<>();
            completedEventCategories = new ArrayList<>();
            completedEventDescriptions = new ArrayList<>();

            // Populate ongoing and completed event ArrayLists
            for (int i = 0; i < eventNames.size(); i++) {
                if ("on_going".equals(eventStatuses.get(i))) {
                    ongoingEventNames.add(eventNames.get(i));
                    ongoingEventDates.add(eventDates.get(i));
                    ongoingEventTimes.add(eventTimes.get(i));
                    ongoingEventStatuses.add(eventStatuses.get(i));
                    ongoingEventCategories.add(eventCategories.get(i));
                    ongoingEventDescriptions.add(eventDescriptions.get(i));

                } else if ("completed".equals(eventStatuses.get(i))) {
                    completedEventNames.add(eventNames.get(i));
                    completedEventDates.add(eventDates.get(i));
                    completedEventTimes.add(eventTimes.get(i));
                    completedEventStatuses.add(eventStatuses.get(i));
                    completedEventCategories.add(eventCategories.get(i));
                    completedEventDescriptions.add(eventDescriptions.get(i));
                }
            }

            Log.d("EventData", "(createAdapters) Ongoing Event Names Size: " + ongoingEventNames.size());
            Log.d("EventData", "(createAdapters) Complete Event Names Size: " + completedEventNames.size());

            // Create and set adapters for the list views
            adapter1 = new Event_List_Adapter(getContext(), ongoingEventNames, ongoingEventDates, ongoingEventTimes, ongoingEventStatuses, ongoingEventCategories, ongoingEventDescriptions, "on_going");
            listView1.setAdapter(adapter1);

            adapter2 = new Event_List_Adapter(getContext(), completedEventNames, completedEventDates, completedEventTimes, completedEventStatuses, completedEventCategories, completedEventDescriptions, "completed");
            listView2.setAdapter(adapter2);

            adapter1.setOnDeleteClickListener(this);
            adapter2.setOnDeleteClickListener(this);
        }
    }

    @Override
    public void onEventDetailsChanged() {
        Log.d("EventData", "(onEventDetailsChanged) Event Names Size: " + eventNames.size());

        // Data has changed, update the UI and create adapters
        if (adapter1 == null || adapter2 == null) {
            createAdapters();
        } else {
            updateAdapters();
        }
        Log.d("StatusUpdate", "Names size from onEventDetailsUpdated: " + eventNames.size());
        editEventStatus();

        // Call the method to set event notifications
        for (int i = 0; i < eventNames.size(); i++) {
            setEventNotifications(eventNames.get(i), eventDates.get(i), eventDescriptions.get(i));
        }
    }

    private void updateAdapters() {
        // Check if the data is available before updating the adapters
        if (eventNames != null && eventDates != null && eventTimes != null && eventStatuses != null &&
                !eventNames.isEmpty() && !eventDates.isEmpty() && !eventTimes.isEmpty() && !eventStatuses.isEmpty()) {
            // Update existing adapters with the new data
            adapter1.updateData(ongoingEventNames, ongoingEventDates, ongoingEventTimes, ongoingEventStatuses, ongoingEventCategories, ongoingEventDescriptions);
            adapter2.updateData(completedEventNames, completedEventDates, completedEventTimes, completedEventStatuses, completedEventCategories, completedEventDescriptions);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventDetailsManager.getInstance().removeEventDetailsListener(this);
    }

    @Override
    public void onDeleteClick(int position) {
        refreshData();
    }

    private void editEventStatus() {
        UserDetailsManager user = UserDetailsManager.getInstance();

        if (user != null && "admin".equals(user.getDesignation()) && !eventDates.isEmpty() && !eventNames.isEmpty() && !eventStatuses.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date currentDate = new Date();

            for (int i = 0; i < eventDates.size(); i++) {
                try {
                    Date eventDate = dateFormat.parse(eventDates.get(i));

                    // Check if the event date is in the past
                    if (eventDate != null && eventDate.before(currentDate)) {
                        // Change the status from on_going to completed
                        if ("on_going".equals(eventStatuses.get(i))) {

                            eventStatuses.set(i, "completed");
                            // Update the event status in the Firestore database
                            updateEventStatusInDatabase(eventNames.get(i));

                        }

                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            refreshData();
        }
    }

    private void updateEventStatusInDatabase(String eventName) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventName)
                .update("status", "completed")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Update_Status", "Event status updated successfully for event: " + eventName);
                        } else {
                            Log.d("Update_Status", "Failed to update event status for event: " + eventName, task.getException());
                        }
                    }
                });
    }

    private void setEventNotifications(String eventName, String eventDate, String eventDescription) {
        // Convert eventDate to Date object (assuming eventDate is in the format "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date eventDateTime = dateFormat.parse(eventDate);

            // Calculate the time difference between current time and event time
            long timeDifference=0;
            if(eventDateTime != null)
                timeDifference = eventDateTime.getTime() - System.currentTimeMillis();

            // Check if the event is within a week or a day
            if (timeDifference > 0) {
                // Event is in the future, schedule notifications
                scheduleNotification(eventName, eventDate, eventDescription, timeDifference - TimeUnit.DAYS.toMillis(1), 1);
                scheduleNotification(eventName, eventDate, eventDescription, timeDifference - TimeUnit.DAYS.toMillis(7), 7);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void scheduleNotification(String eventName, String eventDate, String eventDescription, long delay, int notificationId) {
        Intent notificationIntent = new Intent(getContext(), NotificationReceiver.class);
        notificationIntent.putExtra("eventName", eventName);
        notificationIntent.putExtra("eventDate", eventDate);
        notificationIntent.putExtra("eventDescription", eventDescription);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        assert alarmManager != null;
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pendingIntent);
    }
}

