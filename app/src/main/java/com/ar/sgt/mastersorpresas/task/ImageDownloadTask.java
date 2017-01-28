package com.ar.sgt.mastersorpresas.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.ar.sgt.mastersorpresas.model.utils.BitmapConverter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gabriel on 27/01/2017.
 */
public class ImageDownloadTask extends AsyncTask<URL, Void, Bitmap> {

    private static final String TAG = "ImageDownloadTask";

    private AsyncTaskStatusListener statusListener;

    public ImageDownloadTask(AsyncTaskStatusListener listener) {
        this.statusListener = listener;
    }

    @Override
    protected Bitmap doInBackground(URL... urls) {
        if (urls.length > 1) throw new UnsupportedOperationException("Only 1 URL parameter is supported");

        URL url = urls[0];

        Log.d(TAG, "Download " + url.getPath());

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);

            InputStream input = new BufferedInputStream(urlConnection.getInputStream());

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }

            input.close();

            BitmapConverter converter = new BitmapConverter();

            Log.d(TAG, "Download " + url.getPath() + " complete.");
            Bitmap result = converter.byteArrayToBitmap(output.toByteArray());
            output.close();

            return result;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        statusListener.preExecute();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        statusListener.postExecute(bitmap);
        super.onPostExecute(bitmap);
    }

}
