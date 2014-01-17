package com.kelsos.mbrc.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class RemoteUtils {

    private RemoteUtils() { }

    private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Used to get the current utc ISO-8601 datetime.
     * @return The current utc time in ISO-8601.
     */
    public static String currentTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
