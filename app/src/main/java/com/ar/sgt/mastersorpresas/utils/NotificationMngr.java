package com.ar.sgt.mastersorpresas.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.ar.sgt.mastersorpresas.MainActivity;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.Reminder;

import java.util.List;

/**
 * Created by Gabriel on 16/06/2016.
 */
public class NotificationMngr {

    private static final int NOTIFICATION_ID = 1;

    private static final String NOTIFICATION_GROUP = "SorpresasNotification";

    public static void showNewPromoNotification(Context context, List<Promo> promos) {

        String title = context.getString(R.string.notification_title);

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
                .setSummaryText(title);

        for (Promo p : promos) {
            style.addLine(p.getText());
        }

        NotificationCompat.Builder mBuilder = buildNotification(context)
                        .setContentTitle(title)
                        .setContentText(title)
                        .setGroup(NOTIFICATION_GROUP)
                        .setStyle(style);


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static NotificationCompat.Builder buildNotification(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_giftcard)
                        .setGroupSummary(true)
                        .setAutoCancel(true);

        String ringtone = sharedPreferences.getString("notifications_new_message_ringtone", "");
        if (!TextUtils.isEmpty(ringtone)) {
            Uri sound = Uri.parse(ringtone);
            mBuilder.setSound(sound);
        }
        if (sharedPreferences.getBoolean("notifications_new_message_vibrate", false)) {
            // The first index of array is number of milliseconds to wait before turning the vibrator on and the second index is number of millisecond to vibrate before turning it off
            long[] v = {1000,1000};
            mBuilder.setVibrate(v);
        }

        return mBuilder;
    }

    public static void hideNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    public static void hideNotification(Context context, int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

}
