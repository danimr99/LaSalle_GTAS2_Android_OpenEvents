package com.openevents.utils;

import android.content.Context;
import android.widget.Toast;

import com.openevents.R;

public abstract class ToastNotification {
    public static void showNotification(Context context, Integer messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void showError(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

    public static void showServerConnectionError(Context context) {
        ToastNotification.showNotification(context, R.string.cannotConnectToServerError);
    }
}
