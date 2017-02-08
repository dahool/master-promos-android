package com.ar.sgt.mastersorpresas;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ar.sgt.mastersorpresas.model.DaoMaster;
import com.ar.sgt.mastersorpresas.model.DaoSession;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.greenrobot.greendao.database.Database;

import java.util.UUID;

/**
 * Created by Gabriel on 27/01/2017.
 */
@ReportsCrashes(
        formUri = "https://collector.tracepot.com/6029da33",
        connectionTimeout = 30000,
        mailTo = "gabriel.sgt+sorpresas@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class App extends Application {

    private static final String TAG = "App";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!ACRA.isACRASenderServiceProcess()) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "sorpresas-db");
            Database db = helper.getWritableDb();
            daoSession = new DaoMaster(db).newSession();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (sharedPreferences.getString("INSTANCE-ID", null) == null) {
                sharedPreferences.edit().putString("INSTANCE-ID", UUID.randomUUID().toString()).apply();
            }
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
