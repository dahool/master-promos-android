/**
 * Copyright (c) 2014 Sergio Gabriel Teves
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.ar.sgt.mastersorpresas.pref;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.ar.sgt.mastersorpresas.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Gabriel
 * 
 */
public class NumberPickerPreference extends DialogPreference {

    private NumberPicker mPicker;
    private Context mContext;

    private int mMin, mMax, mValue, mDefaultValue = 0;
    private int mStep = 1;

    private String[] mDisplayValues;

    /**
     * @param context
     * @param attrs
     */
    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray pAttrs = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
        
        // Get picker values:
        mDefaultValue = pAttrs.getInt(R.styleable.NumberPickerPreference_defaultValue, 0);
        mMin = pAttrs.getInt(R.styleable.NumberPickerPreference_min, 0);
        mStep = pAttrs.getInt(R.styleable.NumberPickerPreference_step, 1);
        mMax = pAttrs.getInt(R.styleable.NumberPickerPreference_max, 100);

        if (mStep <= 0) mStep = 1;

        mDisplayValues = getDisplayValues();

        pAttrs.recycle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.preference.DialogPreference#onCreateDialogView()
     */
    @Override
    protected View onCreateDialogView() {
        //LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);

        mPicker = new NumberPicker(mContext);
        mPicker.setDisplayedValues(mDisplayValues);
        mPicker.setMinValue(0);
        mPicker.setMaxValue(mDisplayValues.length-1);
        mPicker.setValue(lookupValue(mValue));
        mPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mPicker.setWrapSelectorWheel(false);

        layout.addView(mPicker, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return layout;
    }

    private int lookupValue(int mValue) {
        for (int i = 0; i < mDisplayValues.length ; i++) {
            if (Integer.parseInt(mDisplayValues[i]) == mValue) return i;
        }
        return 0;
    }

    private String[] getDisplayValues() {
        List<String> n = new ArrayList<>();
        for (int i = mMin; i <= mMax; i+=mStep) {
            n.add(Integer.toString(i));
        }
        return n.toArray(new String[n.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.preference.DialogPreference#onBindDialogView(android.view.View)
     */
    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.preference.Preference#onSetInitialValue(boolean,
     * java.lang.Object)
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(mDefaultValue) : (Integer) defaultValue);
        setSummary(Integer.toString(mValue));
    }

    @Override
    public void showDialog(Bundle state) {
        super.showDialog(state);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            int v = Integer.parseInt(mDisplayValues[mPicker.getValue()]);
            if (!callChangeListener(v)) {
                return;
            }
            setValue(v);
            setSummary(Integer.toString(mValue));
        }
    }

    public void setValue(int mValue) {
        this.mValue = mValue;
        persistInt(mValue);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    /**
     * @return the mMin
     */
    public int getMin() {
        return mMin;
    }

    /**
     * @param mMin the mMin to set
     */
    public void setMin(int mMin) {
        this.mMin = mMin;
    }

    /**
     * @return the mMax
     */
    public int getMax() {
        return mMax;
    }

    /**
     * @param mMax the mMax to set
     */
    public void setMax(int mMax) {
        this.mMax = mMax;
    }

    /**
     * @return the mDefaultValue
     */
    public int getDefaultValue() {
        return mDefaultValue;
    }

    /**
     * @param mDefaultValue the mDefaultValue to set
     */
    public void setDefaultValue(int mDefaultValue) {
        this.mDefaultValue = mDefaultValue;
    }
    
}
