package com.kelsos.mbrc.util;

import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class RemoteUtils {

    private RemoteUtils() {}

    private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String currentTime() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public static int inSampleSize(BitmapFactory.Options options, int width, int height) {
        final int oldWidth = options.outWidth;
        final int oldHeight = options.outHeight;
        int inSampleSize = 1;

        if (oldHeight > height || oldHeight > width) {
            final int halfHeight = oldHeight/2;
            final int halfWidth = oldWidth/2;

            while ((halfHeight/inSampleSize) > height && (halfWidth/inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
