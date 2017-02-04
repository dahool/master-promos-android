package com.ar.sgt.mastersorpresas.pref;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gabriel on 04/02/2017.
 */
public class TimePickerPreference extends DialogPreference {

    private static final String TAG = "TimePickerPreference";

    private String mTime;

    private TimePicker mTimePicker;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        mTimePicker = new TimePicker(getContext());
        mTimePicker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
        return mTimePicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(getHour());
            mTimePicker.setMinute(getMinute());
        } else {
            mTimePicker.setCurrentHour(getHour());
            mTimePicker.setCurrentMinute(getMinute());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            String time;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                time = toTime(mTimePicker.getHour(), mTimePicker.getMinute());
            } else {
                time = toTime(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
            }
            if (!callChangeListener(time)) {
                return;
            }
            setTime(time);
            updateSummary();
        }
    }

    private void updateSummary() {
        setSummary(toDisplayValue(mTime));
    }

    private int getHour() {
        return Integer.parseInt(mTime.split(":")[0]);
    }

    private int getMinute() {
        return Integer.parseInt(mTime.split(":")[1]);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return TextUtils.isEmpty(a.getString(0)) ? getCurrentTimeString() : a.getString(0);
    }

    private String getCurrentTimeString() {
        Calendar c = Calendar.getInstance();
        return String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setTime(restorePersistedValue ? getPersistedString(mTime) : (String) defaultValue);
        updateSummary();
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
        persistString(mTime);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    private String toTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    private String toDisplayValue(String value) {
        if (DateFormat.is24HourFormat(getContext())) return value;
        java.text.DateFormat df = new SimpleDateFormat("HH:mm");
        Date d;
        try {
            d = df.parse(value);
        } catch (ParseException e) {
            return value;
        }
        java.text.DateFormat outTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return outTimeFormat.format(d);
    }

}
