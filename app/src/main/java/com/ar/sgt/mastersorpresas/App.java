package com.ar.sgt.mastersorpresas;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ar.sgt.mastersorpresas.model.DaoMaster;
import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.PromoDao;

import org.greenrobot.greendao.database.Database;

import java.util.UUID;

/**
 * Created by Gabriel on 27/01/2017.
 */
public class App extends Application {

    private static final String TAG = "App";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "sorpresas-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getString("INSTANCE-ID", null) == null) {
            sharedPreferences.edit().putString("INSTANCE-ID", UUID.randomUUID().toString()).apply();
        }

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
