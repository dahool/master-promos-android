package com.ar.sgt.mastersorpresas.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.DaoSession;
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
            reminder.setNextSchedule(null);
            reminderDao.save(reminder);

            String title = reminder.getTitle();

            NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title);

            if (ReminderUtils.isToday(reminder.getDateTo())) {
                style.bigText(context.getString(R.string.reminder_expire_today, reminder.getTitle()));
                style.setSummaryText(context.getString(R.string.reminder_expire_today_short));
            } else {
                style.bigText(context.getString(R.string.reminder_expire, reminder.getTitle(), ReminderUtils.formatDate(reminder.getDateTo())));
                style.setSummaryText(context.getString(R.string.reminder_expire_short, ReminderUtils.formatDate(reminder.getDateTo())));
            }

            NotificationCompat.Builder builder = NotificationMngr.buildNotification(context)
                        .setContentTitle(title)
                        .setContentText(title)
                        .setGroup(TAG)
                        .setGroupSummary(true)
                        .setStyle(style);

            builder.addAction(R.drawable.ic_notification_off, context.getString(R.string.cancel), getActionIntent(context, reminder, ReminderUpdateReceiver.ACTION_CANCEL));

            if (ReminderUtils.canBeScheduled(reminder)) {
                builder.addAction(R.drawable.ic_action_reminder_set, context.getString(R.string.next_reminder), getActionIntent(context, reminder, ReminderUpdateReceiver.ACTION_RETRY));
            }

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(reminder.getId().intValue(), builder.build());
        }

    }

    public PendingIntent getActionIntent(Context context, Reminder reminder, String action) {
        Intent intent = new Intent(context, ReminderUpdateReceiver.class);
        intent.setAction(action + reminder.getId().toString());
        intent.putExtra(REMINDER_KEY, reminder.getId());
        intent.putExtra(ReminderUpdateReceiver.ACTION, action);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
