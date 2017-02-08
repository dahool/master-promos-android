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

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.ReminderDao;

import java.util.Calendar;

/**
 * Created by Gabriel on 03/02/2017.
 */
public class AlarmUtils {

    private static final String TAG = "AlarmUtils";

    public static void scheduleAlarm(Context context, Reminder reminder) {
        scheduleAlarm(context, reminder, null);
    }

    private static String getAlarmTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String time = sharedPreferences.getString("reminder_time", "10:00");
        return time;
    }

    public static void scheduleAlarm(Context context, Reminder reminder, String time) {

        if (reminder.getNextSchedule() == null) return;

        if (time == null) time = getAlarmTime(context);

        int hour = Integer.parseInt(time.split(":")[0]);
        int min = Integer.parseInt(time.split(":")[1]);

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
        return PendingIntent.getBroadcast(context, reminder.getId().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void scheduleAll(Context context) {
        scheduleAlarm(context, null);
    }

    public static void scheduleAll(Context context, String time) {
        App application = (App) context.getApplicationContext();
        DaoSession daoSession = application.getDaoSession();
        ReminderDao reminderDao = daoSession.getReminderDao();

        Long value = ReminderUtils.calendarToLong(ReminderUtils.getCurrent());

        for (Reminder r : reminderDao.queryBuilder().where(ReminderDao.Properties.NextSchedule.isNotNull(), ReminderDao.Properties.NextSchedule.ge(value)).list()) {
            AlarmUtils.scheduleAlarm(context, r, time);
        }

    }

}
