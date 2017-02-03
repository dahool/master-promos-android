package com.ar.sgt.mastersorpresas.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Reminder;

import java.util.Calendar;

/**
 * Created by Gabriel on 03/02/2017.
 */
public class AlarmUtils {

    private static final String TAG = "AlarmUtils";

    public static void scheduleAlarm(Context context, Reminder reminder) {

        if (reminder.getNextSchedule() == null) return;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String time = sharedPreferences.getString("reminder_time", "10:00");

        int hour = Integer.parseInt(time.substring(0, 2));
        int min = Integer.parseInt(time.substring(3,5));

        Calendar cal = ReminderUtils.longToCalendar(reminder.getNextSchedule());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = buildIntent(context, reminder);

        // cancel if already exists
        alarmMgr.cancel(alarmIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
        } else {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
        }

        Log.d(TAG, "Scheduled alarm for " + reminder.getTitle() + " to run on " + cal.getTime());
    }

    public static void cancelAlarm(Context context, Reminder reminder) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(buildIntent(context, reminder));
        Log.d(TAG, "Canceled " + reminder.getTitle());
    }

    private static PendingIntent buildIntent(Context context, Reminder reminder) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.REMINDER_KEY, reminder.getId());
        return PendingIntent.getBroadcast(context, reminder.getId().intValue(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }


}
