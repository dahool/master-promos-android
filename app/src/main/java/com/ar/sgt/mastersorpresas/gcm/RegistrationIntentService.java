package com.ar.sgt.mastersorpresas.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.utils.AndroidUtils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Gabriel on 13/05/2016.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public static final String TOKEN_CURRENT = "current_reg_token";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.i(TAG, "GCM Registration Token: " + token);

            if (token != null && !token.equals(sharedPreferences.getString(TOKEN_CURRENT, "0"))) {
                sendRegistrationToServer(token);
                sharedPreferences.edit().putString(TOKEN_CURRENT, token).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }
    }

    private void sendRegistrationToServer(String token) throws Exception {
        String serviceUrl = getApplicationContext().getString(R.string.REGISTRATION_URL, getApplicationContext().getString(R.string.SERVICE_HOST));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String device = AndroidUtils.getDeviceName();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceId", device + " - " + sharedPreferences.getString("INSTANCE-ID", "0"));
            jsonObject.put("regId", token);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            throw e;
        }

        HttpURLConnection conn = null;
        try {
            String data = jsonObject.toString();
            Log.d(getClass().getSimpleName(), "Submit to " + serviceUrl + ": " + data);

            URL url = new URL(serviceUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            // enable POST
            conn.setDoOutput(true);

            conn.setFixedLengthStreamingMode(data.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(data.getBytes());
            out.flush();
            out.close();

            int status = conn.getResponseCode();

            if (status != 200) {
                Log.e(TAG, conn.getResponseMessage());
                throw new Exception(conn.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "Submit error", e);
            throw e;
        } finally {
            if (conn != null) conn.disconnect();
        }

    }

}
