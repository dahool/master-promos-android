package com.ar.sgt.mastersorpresas.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.utils.AndroidUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Gabriel on 26/01/2017.
 */
public class DataUpdateHandler {

    private static final String TAG = "DataUpdateHandler";

    private Context mContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    public DataUpdateHandler(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public List<Promo> execute() {
        return parseResult(retrieveData());
    }

    private List<Promo> parseResult(JSONArray object) {
        List<Promo> list = null;
        if (object != null) {
            list = new ArrayList<Promo>(object.length());
            for (int i = 0; i < object.length(); i++) {
                try {
                    JSONObject obj = object.getJSONObject(i);
                    list.add(objectMapper.readValue(obj.toString(), Promo.class));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            Log.d(TAG, "Result: " + list.toString());
        }
        return list;
    }

    private JSONArray retrieveData() {

        if (!isNetworkAvaiable()) return null;

        String serviceUrl = getContext().getString(R.string.RETRIEVE_URL, getContext().getString(R.string.SERVICE_HOST));
        String device = AndroidUtils.getDeviceName();

        BufferedReader reader = null;
        HttpURLConnection conn = null;

        try {
            Log.d(TAG, "Call: " + serviceUrl);

            URL url = new URL(serviceUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Log.d(TAG, "RAW Response: " + sb.toString());
            JSONArray response = new JSONArray(sb.toString());
            return response;
        } catch (SocketTimeoutException se) {
            Log.e(TAG, "Timeout", se);
        } catch (Exception e) {
            Log.e(TAG, "Error", e);
        } finally {
            try {
                if (conn != null) conn.disconnect();
                if (reader != null) reader.close();
            } catch (IOException e) {
                //
            }
        }
        return null;
    }

    private boolean isNetworkAvaiable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
