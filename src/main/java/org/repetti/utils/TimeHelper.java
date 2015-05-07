package org.repetti.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author repetti
 */
public class TimeHelper {
    public static final DateFormat standard = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    public static final DateFormat shorty = new SimpleDateFormat("yyMMdd_HHmmss");

    public static String getTime() {
        return standard.format(new Date());
    }

    public static String getTime(DateFormat df) {
        return df.format(new Date());
    }

    public static String getTime(Date date, DateFormat df) {
        return df.format(date);
    }

    public static String getTime(long time) {
        return standard.format(new Date(time));
    }

    public static String toDaySecondDifference(long difference) {
        difference *= difference > 0 ? .001 : -.001;
        if (difference == 0) {
            return "0 seconds";
        }
        StringBuilder ret = new StringBuilder();

        difference = append(ret, difference, 60 * 60 * 24, "day");
        difference = append(ret, difference, 60 * 60, "hour");
        difference = append(ret, difference, 60, "minute");
        append(ret, difference, 1, "second");
        return ret.toString();
    }

    private static long append(StringBuilder sb, long val, long min, String desc) {
        long v = val / min;
        if (v > 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(v).append(" ").append(desc);
            if (v > 1) {
                sb.append("s");
            }
        }
        return val - v * min;
    }
}
