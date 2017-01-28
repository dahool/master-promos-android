package com.ar.sgt.mastersorpresas.gcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.task.DataPersistentHandler;
import com.ar.sgt.mastersorpresas.task.DataUpdateHandler;
import com.ar.sgt.mastersorpresas.task.DataUpdateTask;
import com.ar.sgt.mastersorpresas.utils.NotificationMngr;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class MessageReceiverService extends FirebaseMessagingService {

    private static final String TAG = "MessageReceiverService";

    public MessageReceiverService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, remoteMessage.getMessageId());
        DataUpdateHandler updateHandler = new DataUpdateHandler(getApplicationContext());
        List<Promo> promos = updateHandler.execute();

        DataPersistentHandler persistentHandler = new DataPersistentHandler(getApplication());
        
        if (persistentHandler.persist(promos)) {
            showNotification(promos);
        }
    }

    private void showNotification(List<Promo> promos) {
        Log.d(TAG, "Show notification");
        NotificationMngr.showNotification(getApplicationContext(), promos);
    }

}
