package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RemoteUtils {

  public static String getVersion(Context mContext) throws PackageManager.NameNotFoundException {
    PackageInfo mInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
    return mInfo.versionName;
  }

  public static long getVersionCode(Context mContext) throws PackageManager.NameNotFoundException {
    PackageInfo mInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
    return mInfo.versionCode;
  }

  /**
   * Retrieves the current ISO formatted DateTime.
   *
   * @return Time at this moment in ISO 8601 format
   */
  public static String getUtcNow() {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    df.setTimeZone(TimeZone.getTimeZone("UTC"));
    return df.format(new Date());
  }
}
