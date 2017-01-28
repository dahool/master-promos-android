package com.ar.sgt.mastersorpresas.model.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * Created by Gabriel on 27/01/2017.
 */
public class BitmapConverter {

    private static final String TAG = "BitmapConverter";

    public Bitmap byteArrayToBitmap(byte[] databaseValue) {
        if (databaseValue == null) return null;
        Bitmap result = BitmapFactory.decodeByteArray(databaseValue, 0, databaseValue.length);
        return result;
    }

    public byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byte[] byteArray = byteBuffer.array();
        return byteArray;
    }

}
