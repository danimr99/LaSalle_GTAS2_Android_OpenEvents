package com.openevents.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public abstract class DateHandler {
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

    @SuppressLint("SimpleDateFormat")
    public static String convertDateTimeFromPicker(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String toAPI(String date) {
        try {
            Date d = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(d);
        } catch (ParseException e) {
            return date;
        }
    }

    public static boolean compareDates(String start, String end) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            // Convert strings to dates
            Date d1 = format.parse(start);
            Date d2 = format.parse(end);

            // Check if start is before end
            return Objects.requireNonNull(d1).before(d2);
        } catch (ParseException e) {
            return false;
        }
    }
}
