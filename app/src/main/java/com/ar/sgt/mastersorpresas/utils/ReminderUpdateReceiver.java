package com.ar.sgt.mastersorpresas.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.ReminderDao;

/**
 * Created by Gabriel on 04/02/2017.
 */
public class ReminderUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderUpdateReceiver";

    public static final String ACTION = "notificaiton_action";

    public static final String ACTION_CANCEL = "action_cancel";

    public static final String ACTION_RETRY = "action_retry";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.getAction());
        Long currentKey = intent.getLongExtra(AlarmReceiver.REMINDER_KEY, -1);
        switch (intent.getStringExtra(ACTION)) {
            case ACTION_CANCEL:
                App application = (App) context.getApplicationContext();
                DaoSession daoSession = application.getDaoSession();
                ReminderDao reminderDao = daoSession.getReminderDao();
                Reminder reminder = reminderDao.load(currentKey);
                AlarmUtils.cancelAlarm(context, reminder);
                reminder.setNextSchedule(null);
                reminderDao.save(reminder);
                break;
        }
        NotificationMngr.hideNotification(context, currentKey.intValue());
    }

}
