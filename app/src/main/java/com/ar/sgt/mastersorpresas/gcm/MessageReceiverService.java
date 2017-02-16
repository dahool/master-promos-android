package com.ar.sgt.mastersorpresas.gcm;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.task.DataPersistentHandler;
import com.ar.sgt.mastersorpresas.task.DataUpdateHandler;
import com.ar.sgt.mastersorpresas.utils.NotificationMngr;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class MessageReceiverService extends FirebaseMessagingService {

    private static final String TAG = "MessageReceiverService";

    public MessageReceiverService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, remoteMessage.getMessageId());
        DataUpdateHandler updateHandler = new DataUpdateHandler(getApplicationContext());

        List<Promo> promos = null;

        String rawData = remoteMessage.getData().get("DATA");
        if (!TextUtils.isEmpty(rawData)) {
            JSONArray response = null;
            try {
                Log.d(TAG, "Received " + rawData);
                response = new JSONArray(rawData);
                promos = updateHandler.parseResult(response);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        // failback method
        if (promos == null) {
            Log.i(TAG, "Using fallback method");
            promos = updateHandler.execute();
        }

        DataPersistentHandler persistentHandler = new DataPersistentHandler(getApplication());
        
        if (promos != null && persistentHandler.persist(promos)) {
            showNotification(promos);
        }
    }

    private void showNotification(List<Promo> promos) {
        Log.d(TAG, "Show notification");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean("notifications_new_message", true)) {
            NotificationMngr.showNewPromoNotification(getApplicationContext(), promos);
        }
    }

}
