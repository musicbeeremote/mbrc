package com.kelsos.mbrc.data;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.util.NotificationService;
import com.squareup.otto.Bus;

@Singleton
public class SyncHandler {

    @Inject public SyncHandler(Context mContext, NotificationService mNotification, Bus bus) {
    }

}
