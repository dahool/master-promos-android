package com.ar.sgt.mastersorpresas.utils;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.ReminderDao;

/**
 * Created by Gabriel on 03/02/2017.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    public static final String REMINDER_KEY = "reminder_key";

    @Override
    public void onReceive(Context context, Intent intent) {

        Long currentKey = intent.getLongExtra(REMINDER_KEY, -1);
        App application = (App) context.getApplicationContext();
        DaoSession daoSession = application.getDaoSession();
        ReminderDao reminderDao = daoSession.getReminderDao();

        Reminder reminder = reminderDao.load(currentKey);

        if (reminder != null) {
            reminder.setNextSchedule(ReminderUtils.getNextSchedule(reminder));
            reminderDao.save(reminder);
            AlarmUtils.scheduleAlarm(context, reminder);

            String title = context.getString(R.string.notification_title);

            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(title);

            style.addLine(reminder.getTitle());
            NotificationMngr.showNotification(context, title, style, TAG, reminder.getId().intValue());
        }

    }
}
