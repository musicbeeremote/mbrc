package com.kelsos.mbrc.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class RemoteUtils {

    public static String getVersion(Context mContext) throws PackageManager.NameNotFoundException {
        PackageInfo mInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        return mInfo.versionName;
    }

    public static long getVersionCode(Context mContext) throws PackageManager.NameNotFoundException {
        PackageInfo mInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        return mInfo.versionCode;
    }
}
