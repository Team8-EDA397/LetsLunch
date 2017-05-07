package com.letslunch.agileteam8.letslunch;


import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.letslunch.agileteam8.letslunch.Activities.HomePageActivity;

/**
 * Created by Gustav on 2017-04-03.
 */

public class ReminderAlarmService extends IntentService {
    private static final int NOTIFICATION_ID = 3;

    public ReminderAlarmService() {
        super("ReminderAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Lets Lunch reminder");
        builder.setContentText("What's your lunch plan?");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent notifyIntent = new Intent(this, HomePageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }


}
