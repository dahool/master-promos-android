package com.ar.sgt.mastersorpresas.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ar.sgt.mastersorpresas.model.Reminder;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Gabriel on 03/02/2017.
 */
public class ReminderUtils {

    private static final String TAG = "ReminderUtils";

    private static final SimpleDateFormat DATE_FORMAT;

    private static final SimpleDateFormat LOCAL_DATE_FORMAT;

    private static final TimeZone PROMO_TIMEZONE = TimeZone.getTimeZone("America/Argentina/Buenos_Aires");

    static {
        DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        DATE_FORMAT.setTimeZone(PROMO_TIMEZONE);

        LOCAL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
        LOCAL_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
    }

    public static boolean canBeScheduled(Reminder reminder) {
        if (reminder.getDateTo() == null) return false;

        Calendar calendar = getCurrent();
        Calendar endCalendar = getCurrent();
        endCalendar.setTime(reminder.getDateTo());

        return compare(calendar, endCalendar) < 0;
    }

    public static Calendar getCurrent() {
        return Calendar.getInstance(PROMO_TIMEZONE);
    }

    public static Date parseDate(@NonNull String date) throws ParseException {
        return DATE_FORMAT.parse(date);
    }

    public static String formatDate(@NonNull Date date) {
        return LOCAL_DATE_FORMAT.format(date);
    }

    private static int compare(Calendar date1, Calendar date2) {
        // we only need to compare days, we don't care about times
        Long number1 = calendarToLong(date1);
        Long number2 = calendarToLong(date2);
        return number1.compareTo(number2);
    }

    @NonNull
    public static Long calendarToLong(@NonNull Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        return Long.parseLong(String.format("%04d%02d%02d", year, month, day));
    }

    @NonNull
    public static Calendar longToCalendar(Long value) {
        String strValue = value.toString();

        int year = Integer.parseInt(strValue.substring(0, 4));
        int month = Integer.parseInt(strValue.substring(4, 6)) - 1;
        int day = Integer.parseInt(strValue.substring(6, 8));

        Calendar calendar = getCurrent();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        return calendar;
    }

    @Nullable
    public static Long getNextSchedule(Reminder reminder) {
        if (reminder.getDateTo() == null || reminder.getDateFrom() == null) return null;

        Calendar currentDate = getCurrent();

        Calendar fromCalendar = getCurrent();
        fromCalendar.setTime(reminder.getDateFrom());

        Calendar endCalendar = getCurrent();
        endCalendar.setTime(reminder.getDateTo());

        if (compare(currentDate, fromCalendar) < 0) {
            return calendarToLong(fromCalendar);
        } else if (compare(currentDate, endCalendar) < 0) {
            currentDate.add(Calendar.DATE, 1);
            return calendarToLong(currentDate);
        }
        return null;

    }

    public static Calendar getRepeatTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int time = sharedPreferences.getInt("reminder_repeat_time", 15);
        Calendar calendar = getCurrent();
        calendar.add(Calendar.MINUTE, time);
        return calendar;
    }


    public static boolean isExpired(Reminder reminder) {
        Calendar endCalendar = getCurrent();
        endCalendar.setTime(reminder.getDateTo());

        return (compare(getCurrent(), endCalendar) > 0);
    }

    public static boolean isToday(Date d) {
        Calendar c1 = getCurrent();
        Calendar c2 = getCurrent();
        c2.setTime(d);
        return compare(c1, c2) == 0;
    }

}
