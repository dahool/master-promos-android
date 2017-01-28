package com.ar.sgt.mastersorpresas.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.model.Promo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ImageLoaderService extends IntentService {

    private static final String TAG = "ImageLoaderService";

    private static final String EXTRA_PROMO = "EXTRA_PROMO";

    public ImageLoaderService() {
        super(TAG);
    }

    public static void retrieveImage(Context context, Promo promo) {
        Intent intent = new Intent(context, ImageLoaderService.class);
        intent.putExtra(EXTRA_PROMO, promo);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final Promo promo = (Promo) intent.getSerializableExtra(EXTRA_PROMO);
            handleDownloadAction(promo);
        }
    }

    private void handleDownloadAction(Promo promo) {

        HttpURLConnection urlConnection = null;
        try {
            URL imageUrl = new URL(promo.getUrl());
            urlConnection = (HttpURLConnection) imageUrl.openConnection();
            InputStream input = new BufferedInputStream(urlConnection.getInputStream());

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }

            input.close();

            //promo.setBitmap(output.toByteArray());
            ((App) getApplication()).getDaoSession().getPromoDao().update(promo);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

}
