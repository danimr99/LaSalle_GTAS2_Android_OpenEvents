package com.openevents.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Base64;

import java.io.ByteArrayOutputStream;

public abstract class Base64Image {
    public static String encode(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static Bitmap decode(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
