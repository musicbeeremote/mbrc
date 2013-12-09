package com.kelsos.mbrc.data;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.util.NotificationService;
import com.squareup.otto.Bus;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class SyncHandler {

    private Context mContext;
    private NotificationService mNotification;
    private Bus bus;
    private LibraryDbHelper dbHelper;
    private int numberOfTracks;
    private int currentTrack;
    private Track cachedTrack;

    @Inject public SyncHandler(Context mContext, NotificationService mNotification, Bus bus) {
        this.mContext = mContext;
        this.mNotification = mNotification;
        this.bus = bus;
        dbHelper = new LibraryDbHelper(mContext);
    }
    long start;
    public void initFullSyncProcess(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
        this.currentTrack = 0;
        start = System.currentTimeMillis();
        getNextTrack();
    }


    public void getNextTrack() {
        long elapsed = System.currentTimeMillis() - start;
        Log.d("mbrc-log", String.format("between calls elapsed: %d ms", elapsed));

        if (currentTrack < numberOfTracks) {
            mNotification.librarySyncNotification(numberOfTracks, currentTrack + 1);
            Map<String, Object> syncData = new HashMap<String, Object>();
            syncData.put("type", "meta");
            syncData.put("file", currentTrack);
            bus.post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, syncData)));
            currentTrack++;
        }
        start = System.currentTimeMillis();
    }

    public void updateCover(String image, String hash) {
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput(hash,Context.MODE_PRIVATE);
            outputStream.write(Base64.decode(image, Base64.DEFAULT));
            outputStream.close();
        }  catch (Exception ex) {
            if (BuildConfig.DEBUG)
                Log.d("mbrc-log", "saving cover", ex);
        }

        long cover_id = dbHelper.insertCover(new Cover(hash));
        cachedTrack.setCoverHash(hash);
        cachedTrack.setCoverId(cover_id);
        dbHelper.insertTrack(cachedTrack);
        cachedTrack = null;
        getNextTrack();
    }

    public void createEntry(Track track) {
        if (dbHelper.getCoverId(track.getCoverHash()) < 0) {
            Map<String, String> syncData = new HashMap<String, String>();
            syncData.put("type", "cover");
            syncData.put("hash", track.getHash());
            bus.post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, syncData)));
            cachedTrack = track;
        } else {
            dbHelper.insertTrack(track);
            getNextTrack();
        }
    }
}
