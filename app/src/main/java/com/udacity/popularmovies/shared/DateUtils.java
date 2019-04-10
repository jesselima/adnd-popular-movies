package com.udacity.popularmovies.shared;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressWarnings("unused")
public final class DateUtils {

    private final static String LOG_TAG = DateUtils.class.getSimpleName();

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    Context context;

    /**
     * Allows others classes instantiates a empty DateUtils object.
     */
    public DateUtils() {
    }

    /**
     * This method when called receives a date as String format in this pattern: yyyy-MM-dd
     * and return a date as a String object with this pattern: yyyy
     *
     * @param dateString is the date in a string format is this pattern yyyy-MM-dd
     * @return the date as a string with this new format.
     */
    public static String simpleDateFormat(String dateString) {
        String dateStringFormated = null;
        Date date;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        if (dateString == null) {
            Log.d(LOG_TAG, "Invalid date");
        }
        try {
            date = dateFormat.parse(dateString);
            dateStringFormated = formatDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStringFormated;
    }

    /**
     * This method receives the Date object as input parameter.
     *
     * @param dateObject is the Date object to be formated.
     * @return a String object with the date formated according to the SimpleDateFormat method pattern: yyyy.
     */
    private static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        return dateFormat.format(dateObject);
    }

}
