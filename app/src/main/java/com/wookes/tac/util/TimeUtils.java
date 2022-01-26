package com.wookes.tac.util;

public class TimeUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        long diff = now - time;
        if (diff <= 0) {
            return "0s";
        } else if (diff < MINUTE_MILLIS) {
            return (diff / SECOND_MILLIS) + "s";
        } else if (diff < HOUR_MILLIS) {
            return (diff / MINUTE_MILLIS) + "m";
        } else if (diff < DAY_MILLIS) {
            return (diff / HOUR_MILLIS) + "h";
        } else if (diff < WEEK_MILLIS) {
            return (diff / DAY_MILLIS) + "d";
        } else {
            return diff / WEEK_MILLIS + "w";
        }

    }
}
