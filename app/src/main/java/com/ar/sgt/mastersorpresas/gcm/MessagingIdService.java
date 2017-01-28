package com.ar.sgt.mastersorpresas.gcm;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Gabriel on 24/01/2017.
 */
public class MessagingIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MessagingIdService";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

}
