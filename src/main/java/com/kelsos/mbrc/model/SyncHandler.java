package com.kelsos.mbrc.model;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class SyncHandler {

    private Bus bus;
    private LibraryDbHelper dbHelper;

    @Inject public SyncHandler(Context context, Bus bus) {
        this.bus = bus;
        dbHelper = new LibraryDbHelper(context);
    }

    public void initFullSyncProcess() {
        List<LibraryTrack> trackList = dbHelper.getAllTracks();

        for (LibraryTrack aTrackList : trackList) {
            String file = aTrackList.getFile();
            Map<String, String> syncData = new HashMap<String, String>();
            syncData.put("type", "meta");
            syncData.put("file", file);
            bus.post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, syncData)));
        }
    }

    public void createEntry(String url) {
        dbHelper.createLibraryEntry(url);
    }
}
