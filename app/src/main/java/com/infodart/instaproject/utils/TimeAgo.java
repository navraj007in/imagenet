package com.infodart.instaproject.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by navrajsingh on 11/15/16.
 */

public class TimeAgo {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = Calendar.getInstance().getTimeInMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday at " + Utils.TimestampTotDate(time,"hh:mm a");
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    private final Calendar mCalendar;

    public TimeAgo(Calendar calendar) {
        mCalendar = calendar;
    }

    @Override
    public String toString() {
        Calendar today = Calendar.getInstance(Locale.getDefault());
        int dayOfYear = mCalendar.get(Calendar.DAY_OF_YEAR);
        if (Math.abs(dayOfYear - today.get(Calendar.DAY_OF_YEAR)) < 2) {
            return getRelativeDay(today);
        }

        return getWeekDay();
    }

    private String getRelativeDay(Calendar today) {
        return DateUtils.getRelativeTimeSpanString(
                mCalendar.getTimeInMillis(),
                today.getTimeInMillis(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_SHOW_WEEKDAY).toString();
    }

    private String getWeekDay() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        return dayFormat.format(mCalendar.getTimeInMillis());
    }
}
