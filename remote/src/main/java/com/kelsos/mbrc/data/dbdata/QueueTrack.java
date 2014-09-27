package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.interfaces.NowPlayingTrackColumns;
import org.codehaus.jackson.JsonNode;
@DatabaseTable(tableName = QueueTrack.TABLE_NAME)
public class QueueTrack extends DataItem implements NowPlayingTrackColumns {
    public static final String TABLE_NAME = "now_playing";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.kelsos.mbrc.provider." + TABLE_NAME;
    public static final int BASE_URI_CODE = 0x3a2a7c3;
    public static final int BASE_ITEM_CODE = 0x3621d5d;
    @DatabaseField
    private String artist;
    @DatabaseField
    private String title;
    @DatabaseField
    private String path;
    @DatabaseField
    private int position;
    @DatabaseField(generatedId = true)
    private long id;

    public QueueTrack(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public QueueTrack(JsonNode jNode) {
        this.artist = jNode.path("artist").getTextValue();
        this.title = jNode.path("title").getTextValue();
        this.path = jNode.path("path").getTextValue();
        this.position = jNode.path("index").asInt(0);
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME +
                LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME, BASE_URI_CODE);
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/#", BASE_ITEM_CODE);
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    @Override
    public ContentValues getContentValues() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        boolean rValue = false;
        if (o instanceof QueueTrack) {
            QueueTrack track = (QueueTrack) o;
            if (track.getTitle().equals(this.title) && track.getArtist().equals(this.artist)) {
                rValue = true;
            }
        }
        return rValue;
    }

    @Override
    public int hashCode() {
        int hash = 0x109;
        hash = hash * 31 + title.hashCode();
        hash = hash * 13 + artist.hashCode();
        return hash;
    }
}
