package com.kelsos.mbrc.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RemoteUtils {
    private static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getVersion(Context mContext) throws PackageManager.NameNotFoundException {
        PackageInfo mInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        return mInfo.versionName;
    }

    public static long getVersionCode(Context mContext) throws PackageManager.NameNotFoundException {
        PackageInfo mInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        return mInfo.versionCode;
    }

    public static String Now() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
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
