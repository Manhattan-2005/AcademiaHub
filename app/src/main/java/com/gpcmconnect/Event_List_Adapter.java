package com.gpcmconnect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Event_List_Adapter extends BaseAdapter {

    Context context;
    ArrayList<String> names, dates, times, statuses, categories, descriptions;
    String tab;

    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public Event_List_Adapter(Context context, ArrayList<String> names, ArrayList<String> dates, ArrayList<String> times, ArrayList<String> statuses, ArrayList<String> categories, ArrayList<String> descriptions, String tab) {
        this.context = context;
        this.names = names;
        this.dates = dates;
        this.times = times;
        this.statuses = statuses;
        this.categories = categories;
        this.descriptions = descriptions;
        this.tab = tab;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        // Return the item only if it matches the tab
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.events_view, null);

            UserDetailsManager userDetailsManager = UserDetailsManager.getInstance();

            // Retrieve views from the convertView
            LinearLayout layout = convertView.findViewById(R.id.layout);
            TextView name = convertView.findViewById(R.id.name);
            TextView date_up = convertView.findViewById(R.id.dateup);
            TextView date = convertView.findViewById(R.id.date);
            TextView time = convertView.findViewById(R.id.time);
            TextView category = convertView.findViewById(R.id.category);
            TextView description = convertView.findViewById(R.id.description);
            ImageView delete = convertView.findViewById(R.id.delete);

            // Set data to views outside the condition
            Log.d("EventData", "(getView form Adapter) Name: " + names.get(position));
            name.setText(names.get(position));
            date_up.setText(String.format("%s", dates.get(position)));
            date.setText(String.format("Date: %s", dates.get(position)));
            time.setText(String.format("Time: %s", times.get(position)));
            category.setText(String.format("Category: %s", categories.get(position)));
            description.setText(String.format("Description: %s", descriptions.get(position)));

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (date.getVisibility() == View.GONE) {
                        date_up.setVisibility(View.GONE);
                        date.setVisibility(View.VISIBLE);
                        time.setVisibility(View.VISIBLE);
                        category.setVisibility(View.VISIBLE);
                        description.setVisibility(View.VISIBLE);
                        if(userDetailsManager.getDesignation().equals("admin")) {
                            delete.setVisibility(View.VISIBLE);
                        }
                    } else {
                        date_up.setVisibility(View.VISIBLE);
                        date.setVisibility(View.GONE);
                        time.setVisibility(View.GONE);
                        category.setVisibility(View.GONE);
                        description.setVisibility(View.GONE);
                        if(userDetailsManager.getDesignation().equals("admin")) {
                            delete.setVisibility(View.GONE);
                        }
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEventFromFirestore(names.get(position));
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onDeleteClick(position);
                    }
                }
            });
        }
        return convertView;
    }

    private void deleteEventFromFirestore(String eventName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Reference to the event document
            DocumentReference eventRef = db.collection("events").document(eventName);

            // Delete the event document
            eventRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Event_Deletion", "Event successfully deleted!");
                            Toast.makeText(context, "Event Deleted Successfully!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Event_Deletion", "Error deleting event", e);
                            Toast.makeText(context, "Error while deleting event! Try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void updateData(ArrayList<String> names, ArrayList<String> dates, ArrayList<String> times, ArrayList<String> statuses, ArrayList<String> categories, ArrayList<String> descriptions) {
        this.names = names;
        this.dates = dates;
        this.times = times;
        this.statuses = statuses;
        this.categories = categories;
        this.descriptions = descriptions;
        notifyDataSetChanged();
    }

}
