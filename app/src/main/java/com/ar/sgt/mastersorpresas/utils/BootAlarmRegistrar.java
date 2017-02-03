package com.ar.sgt.mastersorpresas.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.ReminderDao;

/**
 * Created by Gabriel on 03/02/2017.
 */
public class BootAlarmRegistrar extends BroadcastReceiver {

    private static final String TAG = "BootAlarmRegistrar";

    @Override
    public void onReceive(Context context, Intent intent) {

        App application = (App) context.getApplicationContext();
        DaoSession daoSession = application.getDaoSession();
        ReminderDao reminderDao = daoSession.getReminderDao();

        Long value = ReminderUtils.calendarToLong(ReminderUtils.getCurrent());

        for (Reminder r : reminderDao.queryBuilder().where(ReminderDao.Properties.NextSchedule.isNotNull(), ReminderDao.Properties.NextSchedule.ge(value)).list()) {
            AlarmUtils.scheduleAlarm(context, r);
        }

        // notify missed alarms
        for (Reminder r : reminderDao.queryBuilder().where(ReminderDao.Properties.NextSchedule.isNotNull(), ReminderDao.Properties.NextSchedule.lt(value)).list()) {
            Intent newIntent = new Intent(context, AlarmReceiver.class);
            newIntent.putExtra(AlarmReceiver.REMINDER_KEY, r.getId());
            context.sendBroadcast(newIntent);
        }

    }
}
