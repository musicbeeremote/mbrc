package com.kelsos.mbrc.providers;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.kelsos.mbrc.data.db.CacheHelper;

public class CacheHelperProvider implements Provider<CacheHelper> {

    private final Context mContext;
    @Inject
    public CacheHelperProvider(Context context){
        mContext = context;
    }

    @Override
    public CacheHelper get() {
        return OpenHelperManager.getHelper(mContext, CacheHelper.class);
    }
}
