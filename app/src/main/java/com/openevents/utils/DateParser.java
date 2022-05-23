package com.openevents.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateParser {
    public static String toDate(String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date d = sdf.parse(date);
            return output.format(d != null ? d : date);
        } catch (ParseException exception){
            return date;
        }
    }

    public static String toDateTime(String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date d = sdf.parse(date);
            return output.format(d != null ? d : date);
        } catch (ParseException exception){
            return date;
        }
    }

    public static String convertDateTimeFromPicker(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }
}
