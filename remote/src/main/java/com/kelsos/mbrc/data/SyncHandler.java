package com.kelsos.mbrc.data;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Base64;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.data.dbdata.Genre;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.util.NotificationService;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
import com.squareup.otto.Bus;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class SyncHandler {

    private static final Logger logger = LoggerManager.getLogger();
    private Context mContext;
    private NotificationService mNotification;
    private Bus bus;

    @Inject public SyncHandler(Context mContext, NotificationService mNotification, Bus bus) {
        this.mContext = mContext;
        this.mNotification = mNotification;
        this.bus = bus;


    }

    /**
     * Sends a request to get the next part of the library data.
     * @param total Represents the total number of tracks available.
     * @param offset Represents the index of the starting track.
     * @param limit Represents the number of tracks contained to the message.
     */
    public void getNextBatch(int total, int offset, int limit) {

        if ((offset + limit) < total) {

            mNotification.librarySyncNotification(total, offset);
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("type", "meta");
            syncData.put("offset", offset + limit);
            syncData.put("limit", limit);
            bus.post(new MessageEvent(ProtocolEventType.USER_ACTION,
                    new UserAction(Protocol.LIBRARY, syncData)));
        } else {
            mNotification.librarySyncNotification(total, total);
            ContentResolver contentResolver = mContext.getContentResolver();
            contentResolver.notifyChange(Track.getContentUri(), null, false);
            contentResolver.notifyChange(Album.getContentUri(), null, false);
            contentResolver.notifyChange(Artist.getContentUri(), null, false);
            contentResolver.notifyChange(Genre.getContentUri(), null, false);
            requestNextBatch(0,5);
        }

    }

    public void setCovers() {

    }

    public void updateCover(String image, String hash) {
        if (hash == null || hash.equals("")) {
            return;
        }
        FileOutputStream outputStream;
        try {
            outputStream = mContext.openFileOutput(hash, Context.MODE_PRIVATE);
            outputStream.write(Base64.decode(image, Base64.DEFAULT));
            outputStream.close();
        }  catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                logger.d("saving cover", ex);
            }
        }
    }

    public void requestNextBatch(int offset, int limit) {
        Map<String, Object> syncData = new HashMap<>();
        syncData.put("type", "cover");
        syncData.put("offset", offset + limit);
        syncData.put("limit", limit);
        bus.post(new MessageEvent(ProtocolEventType.USER_ACTION,
                new UserAction(Protocol.LIBRARY, syncData)));

    }

    /**
     * Requests the Queue tracks from the plugin
     * @param limit Represents the number of tracks contained to the message.
     * @param offset Represents the index of the starting track.
     */
    public void getQueueTracks(int limit, int offset) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("type", "list");
        message.put("offset", offset + limit);
        message.put("limit", limit);
        bus.post(new MessageEvent(ProtocolEventType.USER_ACTION,
                new UserAction(Protocol.NOW_PLAYING, message)));
    }
}
