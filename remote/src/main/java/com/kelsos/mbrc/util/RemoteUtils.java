package com.kelsos.mbrc.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class RemoteUtils {

    private RemoteUtils() { }

    public static String getTimeStamp() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public static boolean isNullOrEmpty(String string){
        return (string == null || string.equals(""));
    }
}
