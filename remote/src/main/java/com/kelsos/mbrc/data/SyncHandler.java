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

    public static final int BATCH_SIZE = 50;
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

    public void initFullSyncProcess(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
        this.currentTrack = 0;
        getNextTrack();
    }


    public void getNextTrack() {

        if (currentTrack < numberOfTracks) {
            mNotification.librarySyncNotification(numberOfTracks, currentTrack + 1);
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("type", "meta");
            syncData.put("file", currentTrack);
            bus.post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, syncData)));
            currentTrack++;
        }
    }

    public void getNextBunch() {

        if (currentTrack < numberOfTracks) {
            mNotification.librarySyncNotification(numberOfTracks, currentTrack + BATCH_SIZE);
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("type", "meta");
            syncData.put("file", currentTrack);
            bus.post(new MessageEvent(ProtocolEventType.UserAction,
                    new UserAction(Protocol.LibrarySync, syncData)));
            currentTrack += BATCH_SIZE;
        }
    }

    public void updateCover(String image, String hash) {
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput(hash, Context.MODE_PRIVATE);
            outputStream.write(Base64.decode(image, Base64.DEFAULT));
            outputStream.close();
        }  catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "saving cover", ex);
            }
        }

        long coverId = dbHelper.insertCover(new Cover(hash));
        cachedTrack.setCoverHash(hash);
        cachedTrack.setCoverId(coverId);
        dbHelper.insertTrack(cachedTrack);
        cachedTrack = null;
        getNextTrack();
    }

    public void createEntry(Track track) {
//        if (dbHelper.getCoverId(track.getCoverHash()) <= 0) {
//            Map<String, String> syncData = new HashMap<>();
//            syncData.put("type", "cover");
//            syncData.put("hash", track.getHash());
//            bus.post(new MessageEvent(ProtocolEventType.UserAction,
//                    new UserAction(Protocol.LibrarySync, syncData)));
//            cachedTrack = track;
//        } else {
        dbHelper.insertTrack(track);
//        }
    }
}
