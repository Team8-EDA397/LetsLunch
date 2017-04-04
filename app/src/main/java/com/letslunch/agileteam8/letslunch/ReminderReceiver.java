package com.letslunch.agileteam8.letslunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Gustav on 2017-04-03.
 */

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, ReminderAlarmService.class);
        context.startService(service1);

    }
}
