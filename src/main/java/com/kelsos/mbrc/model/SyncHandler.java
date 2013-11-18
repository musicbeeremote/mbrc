package com.kelsos.mbrc.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class SyncHandler {

    private Context mContext;
    private Bus bus;
    private LibraryDbHelper dbHelper;
    private int numberOfTracks;
    private int currentTrack;
    private LibraryTrack cachedTrack;

    @Inject public SyncHandler(Context mContext, Bus bus) {
        this.mContext = mContext;
        this.bus = bus;
        dbHelper = new LibraryDbHelper(mContext);
    }

    public void initFullSyncProcess(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
        this.currentTrack = 0;
        getNextTrack();
    }

    public void getNextTrack() {
        if (currentTrack < numberOfTracks) {
            Map<String, Object> syncData = new HashMap<String, Object>();
            syncData.put("type", "meta");
            syncData.put("file", currentTrack);
            bus.post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, syncData)));
            currentTrack++;
        }
    }

    public void updateCover(String image, String sha1, int length) {

        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput(sha1,Context.MODE_PRIVATE);
            outputStream.write(Base64.decode(image, Base64.DEFAULT));
            outputStream.close();
        }  catch (Exception ex) {
            if (BuildConfig.DEBUG)
                Log.d("mbrc-log", "saving cover", ex);
        }

        long cover = dbHelper.insertCover(sha1,length);
        dbHelper.createLibraryEntry(cachedTrack);
        cachedTrack = null;
        getNextTrack();
    }

    public void createEntry(LibraryTrack track) {

        if (dbHelper.getCoverId(track.getCover()) < 0) {
            Map<String, String> syncData = new HashMap<String, String>();
            syncData.put("type", "cover");
            syncData.put("hash", track.getSha1());
            bus.post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, syncData)));
            cachedTrack = track;
        } else {
            dbHelper.createLibraryEntry(track);
            getNextTrack();
        }

    }
}
