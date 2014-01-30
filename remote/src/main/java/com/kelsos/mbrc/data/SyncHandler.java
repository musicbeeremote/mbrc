package com.kelsos.mbrc.data;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.db.LibraryDbHelper;
import com.kelsos.mbrc.data.dbdata.*;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.util.NotificationService;
import com.squareup.otto.Bus;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class SyncHandler {

    public static final int BATCH_SIZE = 50;
    private Context mContext;
    private NotificationService mNotification;
    private Bus bus;
    private LibraryDbHelper dbHelper;
    private int numberOfTracks;
    private int offset;
    private Track cachedTrack;

    @Inject public SyncHandler(Context mContext, NotificationService mNotification, Bus bus) {
        this.mContext = mContext;
        this.mNotification = mNotification;
        this.bus = bus;
        dbHelper = new LibraryDbHelper(mContext);
    }

    public void initFullSyncProcess(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
        this.offset = 0;
        getNextBatch();
    }


    public void getNextBatch() {

        if (offset < numberOfTracks) {

            mNotification.librarySyncNotification(numberOfTracks, offset);
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("type", "meta");
            syncData.put("offset", offset);
            syncData.put("limit", BATCH_SIZE);
            bus.post(new MessageEvent(ProtocolEventType.USER_ACTION,
                    new UserAction(Protocol.LIBRARY_SYNC, syncData)));
            offset += BATCH_SIZE;
        } else {
            mNotification.librarySyncNotification(numberOfTracks, numberOfTracks);
            ContentResolver contentResolver = mContext.getContentResolver();
            contentResolver.notifyChange(Track.getContentUri(), null, false);
            contentResolver.notifyChange(Album.getContentUri(), null, false);
            contentResolver.notifyChange(Artist.getContentUri(), null, false);
            contentResolver.notifyChange(Genre.getContentUri(), null, false);
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
                Log.d(BuildConfig.PACKAGE_NAME, "saving cover", ex);
            }
        }

        long coverId = dbHelper.insertCover(new Cover(hash));
        cachedTrack.setCoverHash(hash);
        cachedTrack.setCoverId(coverId);
        dbHelper.insertTrack(cachedTrack);
        cachedTrack = null;
    }

    public void processBatch(final List<Track> trackList) {
        dbHelper.processBatch(trackList);
    }
}
