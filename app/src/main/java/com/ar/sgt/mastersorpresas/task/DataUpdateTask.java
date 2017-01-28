package com.ar.sgt.mastersorpresas.task;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.utils.AndroidUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 26/01/2017.
 */
public class DataUpdateTask extends AsyncTask<Void, Void, List<Promo>> {

    private static final String TAG = "DataUpdateTask";

    private App mApplication;

    private AsyncTaskStatusListener statusListener = null;

    public DataUpdateTask(Application app) {
        this.mApplication = (App) app;
    }

    public App getApplication() {
        return mApplication;
    }

    @Override
    protected List<Promo> doInBackground(Void... voids) {
        DataUpdateHandler updateHandler = new DataUpdateHandler(getApplication().getApplicationContext());
        return updateHandler.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (statusListener != null) statusListener.preExecute();
    }

    @Override
    protected void onPostExecute(List<Promo> list) {

        DataPersistentHandler persistentHandler = new DataPersistentHandler(getApplication());
        persistentHandler.persist(list);

        if (statusListener != null) {
            statusListener.postExecute(list);
        }

        super.onPostExecute(list);
    }

    public void setStatusListener(AsyncTaskStatusListener statusListener) {
        this.statusListener = statusListener;
    }

}
