package com.openevents.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.openevents.R;

public abstract class Notification {
    public static void showDialogNotification(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setPositiveButton(R.string.acceptLabel, (dialog, button) -> dialog.dismiss());

        builder.setOnDismissListener(DialogInterface::dismiss);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
