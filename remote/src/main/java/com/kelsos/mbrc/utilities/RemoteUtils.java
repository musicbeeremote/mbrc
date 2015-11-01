package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import com.kelsos.mbrc.BuildConfig;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RemoteUtils {

  private static final File SD_CARD = Environment.getExternalStorageDirectory();
  private static final File CACHE = new File(
      String.format("%s/Android/data/%s/cache", SD_CARD.getAbsolutePath(),
          BuildConfig.APPLICATION_ID));

  private RemoteUtils() {
  }

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

  public static String getTimeStamp() {
    final SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());
    return simpleDateFormat.format(new Date());
  }

  public static File getStorage() {
    return CACHE;
  }
}
