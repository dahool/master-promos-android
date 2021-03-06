package com.ar.sgt.mastersorpresas.utils;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.ReminderDao;

import java.util.Calendar;

/**
 * Created by Gabriel on 04/02/2017.
 */
public class ReminderUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderUpdateReceiver";

    public static final String ACTION = "notificaiton_action";

    public static final String ACTION_CANCEL = "action_cancel";

    public static final String ACTION_RETRY = "action_retry";

    public static final String ACTION_LATER = "action_later";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.getAction());
        Long currentKey = intent.getLongExtra(AlarmReceiver.REMINDER_KEY, -1);

        final App application = (App) context.getApplicationContext();
        final DaoSession daoSession = application.getDaoSession();
        final ReminderDao reminderDao = daoSession.getReminderDao();
        Reminder reminder = null;

        switch (intent.getStringExtra(ACTION)) {
            /*case ACTION_CANCEL:
                reminder = reminderDao.load(currentKey);
                AlarmUtils.cancelAlarm(context, reminder);
                reminder.setNextSchedule(null);
                reminderDao.save(reminder);
                break;*/
            case ACTION_LATER:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                int time = sharedPreferences.getInt("reminder_repeat_time", 15);

                reminder = reminderDao.load(currentKey);
                reminder.setNextSchedule(ReminderUtils.getNextSchedule(reminder));
                reminderDao.save(reminder);
                Calendar cal = ReminderUtils.getRepeatTime(context);
                AlarmUtils.scheduleAlarm(context, cal, reminder);

                Toast.makeText(context, context.getString(R.string.repeat_alarm_set, Integer.toString(time)), Toast.LENGTH_LONG).show();
                break;
            case ACTION_RETRY:
                reminder = reminderDao.load(currentKey);
                reminder.setNextSchedule(ReminderUtils.getNextSchedule(reminder));
                reminderDao.save(reminder);
                AlarmUtils.scheduleAlarm(context, reminder);
                break;
        }
        NotificationMngr.hideNotification(context, currentKey.intValue());
    }

}
