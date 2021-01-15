package com.ubtedu.alpha1x.utils;


/**
 * Created by qinicy on 2017/7/5.
 */

public class TimeUtil {
    public static String milliseconds2String(long duration) {
        if (duration < 1000) {
            duration = 1000;
        }
        long m = duration % 1000;
        if (m >= 500) {
            duration += 500;
        }
        return seconds2String(duration / 1000);
    }

    /**
     * Turn, e.g. 67 seconds into "01:07"
     *
     * @param seconds seconds
     * @return
     */
    public static String seconds2String(long seconds) {
        if (seconds <= 0) {
            return "00:00";
        }
        String hourStr = "";
        long hour = seconds / 3600;
        if (hour > 0) {
            if (hour < 10) {
                hourStr = "0" + hour + ":";
            } else {
                hourStr = "" + hour + ":";
            }
            seconds -= 3600 * hour;
        }

        String m = "" + (seconds / 60);
        String s = "" + (seconds % 60);
        if ((seconds % 60) < 10) {
            s = "0" + s;
        }

        if ((seconds / 60) < 10) {
            m = "0" + m;
        }
        String timecodeStr = hourStr + m + ":" + s;
        return timecodeStr;
    }
}
