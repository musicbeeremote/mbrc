package com.kelsos.mbrc.model;

import android.provider.BaseColumns;

public final class SongContract {
    public SongContract() {}

    public static abstract class SongEntry implements BaseColumns {
        public static final String TABLE_NAME ="library";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_FILE = "file";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_ALBUMARTIST = "albumartist";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ALBUM = "album";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_GENRE = "genre";
        public static final String COLUMN_NAME_COVER = "cover";
    }
}
