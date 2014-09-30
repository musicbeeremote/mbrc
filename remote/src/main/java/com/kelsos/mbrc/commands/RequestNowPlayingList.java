package com.kelsos.mbrc.commands;

import android.content.Context;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.SyncHandler;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class RequestNowPlayingList implements ICommand {
    private static final String TAG = RequestNowPlayingList.class.getCanonicalName();
    private SyncHandler mHandler;

    @Inject public RequestNowPlayingList(Context context, SyncHandler mHandler) {
        this.mHandler = mHandler;
    }

    @Override public void execute(final IEvent e) {

    }
}
