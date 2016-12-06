package com.blogspot.junmond.exchangerateyo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by HyunjunLee on 2016-12-06.
 */

public class BootManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        String action = arg1.getAction();
        if(action.equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent alarmIntent = new Intent(arg0, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(arg0, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            SettingManager.initSettingManager(arg0);
            int interval = SettingManager.getInterval();
            AlarmManager am = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
            Log.d("BootReceiver", "set alarm manager with interval : " + interval);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        }
    }
}
