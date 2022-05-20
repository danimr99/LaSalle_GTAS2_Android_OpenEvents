package com.openevents.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateParser {
    public static String toDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date d = sdf.parse(date);
            return output.format(d);
        } catch (ParseException exception){
            return date;
        }
    }

    public static String toDateTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date d = sdf.parse(date);
            return output.format(d);
        } catch (ParseException exception){
            return date;
        }
    }
}
