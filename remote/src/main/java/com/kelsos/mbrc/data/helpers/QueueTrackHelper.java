package com.kelsos.mbrc.data.helpers;

import android.database.Cursor;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dao.QueueTrackDao;

public final class QueueTrackHelper {

    private QueueTrackHelper() { }

    public static final String PATH = QueueTrackDao.Properties.Path.columnName;
    public static final String INDEX = QueueTrackDao.Properties.Position.columnName;
    public static final String TITLE = QueueTrackDao.Properties.Title.columnName;
    public static final String ARTIST = QueueTrackDao.Properties.Artist.columnName;
    public static final String ID = QueueTrackDao.Properties.Id.columnName;

    public static final String[] PROJECTION = {
            ID,
            ARTIST,
            TITLE,
            INDEX,
            PATH
    };

    public static QueueTrack fromCursor(Cursor data) {
        final QueueTrack track = new QueueTrack();
        track.setId(data.getLong(data.getColumnIndex(ID)));
        track.setArtist(data.getString(data.getColumnIndex(ARTIST)));
        track.setTitle(data.getString(data.getColumnIndex(TITLE)));
        track.setPath(data.getString(data.getColumnIndex(PATH)));
        track.setPosition(data.getInt(data.getColumnIndex(INDEX)));
        return track;
    }
}
