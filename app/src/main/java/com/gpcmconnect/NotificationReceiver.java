package com.gpcmconnect;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.gpcmconnect.R;

public class NotificationReceiver extends BroadcastReceiver {

    static final int NOTIFICATION_ID = 69;
    static final String CHANNEL_ID = "Event Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("eventName");
        String eventDate = intent.getStringExtra("eventDate");
        String eventDescription = intent.getStringExtra("eventDescription");

        // Create a notification channel (for Android Oreo and above)
        createNotificationChannel(context);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.account_image_icon)
                .setContentTitle("Event: " + eventName)
                .setContentText("Event Date: " + eventDate)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(eventDescription))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Get the NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notify
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        CharSequence name = "Event Notifications";
        String description = "Receive notifications for upcoming events";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        // Register the channel with the system
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
