package com.blogspot.junmond.exchangerateyo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by HyunjunLee on 2016-11-16.
 */

public class Notifier {

    private Context parentContext = null;

    public Notifier(Context parent)
    {
        this.parentContext = parent;
    }

    public void NotifyToUser(String Title, String Text)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(parentContext)
                        .setSmallIcon(R.drawable.ic_stat_notify)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentTitle(Title)
                        .setContentText(Text);

        Intent resultIntent = new Intent(parentContext, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(parentContext);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) parentContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(5916, mBuilder.build());
    }

}
