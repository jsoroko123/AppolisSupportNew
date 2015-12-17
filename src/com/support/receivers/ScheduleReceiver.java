package com.support.receivers;

/**
 * Created by Jeffery on 8/8/2015.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.support.main.CaseService;


public class ScheduleReceiver extends BroadcastReceiver {

    // restart service every 30 seconds
    private static final long REPEAT_TIME = 1000 * 5;

    @Override
    public void onReceive(Context context, Intent intent) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        int minutes = prefs.getInt("interval");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, CaseService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.cancel(pi);
        // by my own convention, minutes <= 0 means notifications are disabled
        if (1 > 0) {
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 1*60*1000,
                    1*60*1000, pi);
        }



    }
}