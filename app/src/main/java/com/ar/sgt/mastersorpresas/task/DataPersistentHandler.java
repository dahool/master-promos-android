package com.ar.sgt.mastersorpresas.task;

import android.app.Application;
import android.util.Log;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.PromoDao;

import java.util.List;

/**
 * Created by Gabriel on 27/01/2017.
 */
public class DataPersistentHandler {

    private static final String TAG = "DataPersistentHandler";

    private App mApplication;

    public DataPersistentHandler(Application app) {
        this.mApplication = (App) app;
    }

    public App getApplication() {
        return mApplication;
    }

    public boolean persist(List<Promo> promos) {
        if (promos == null) return false;

        Log.d(TAG, "Persist: " + promos.toString());

        DaoSession daoSession = getApplication().getDaoSession();
        PromoDao promoDao = daoSession.getPromoDao();

        boolean updated = false;

        if (promos.isEmpty()) {
            promoDao.deleteAll();
        } else {
            if (promoDao.count() > 0) {
                for (Promo p : promoDao.loadAll()) {
                    if (promos.contains(p)) {
                        Promo newPromo = promos.get(promos.indexOf(p));
                        if (!newPromo.getImage().equals(p.getImage())) {
                            p.setImage(newPromo.getImage());
                            p.setBitmap(null);
                            Log.d(TAG, "Update: " + p);
                            promoDao.update(p);
                        }
                    } else {
                        promoDao.delete(p);
                    }
                }
            }

            for (Promo p : promos) {
                Promo current = promoDao.load(p.getKey());
                if (current == null) {
                    Log.d(TAG, "Insert: " + p);
                    promoDao.insert(p);
                    updated = true;
                }
            }
        }

        Log.d(TAG, "Updated: " + updated);
        return updated;
    }
}
