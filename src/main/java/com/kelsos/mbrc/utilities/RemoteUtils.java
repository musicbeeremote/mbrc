package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RemoteUtils {

    private Context mContext;

    @Inject public RemoteUtils(Context mContext) {
        this.mContext = mContext;
    }

    public String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo mInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        return mInfo.versionName;
    }
}
