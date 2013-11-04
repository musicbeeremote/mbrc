package com.kelsos.mbrc.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LibraryDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TrackLibrary.db";

    private static final String TABLE_LIBRARY ="library";
    private static final String TABLE_GENRES = "genres";
    private static final String TABLE_ARTISTS = "artists";
    private static final String TABLE_ALBUMS = "albums";

    private static final String ENTRY_ID = "id";
    private static final String FILE = "file";
    private static final String ARTIST = "artist";
    private static final String ALBUMARTIST = "albumartist";
    private static final String TITLE = "title";
    private static final String ALBUM = "album";
    private static final String YEAR = "year";
    private static final String GENRE = "genre";
    private static final String COVER = "cover";
    private static final String UPDATED = "updated";
    private static final String NAME = "name";

    private static final String CREATE_TABLE_GENRES = "CREATE TABLE " +
            TABLE_GENRES + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            NAME + " TEXT" + ")";

    private static final String CREATE_TABLE_ARTISTS = "CREATE TABLE " +
            TABLE_ARTISTS + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            NAME + " TEXT" + ")";

    private static final String CREATE_TABLE_ALBUMS = "CREATE TABLE " +
            TABLE_ALBUMS + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            NAME + " TEXT" + ")";


    private static final String CREATE_TABLE_LIBRARY = "CREATE TABLE "
            + TABLE_LIBRARY + "(" + ENTRY_ID + " INTEGER PRIMARY KEY," +
            FILE + " TEXT UNIQUE," + ARTIST + " INTEGER," + ALBUMARTIST + " INTEGER," +
            TITLE + " TEXT," + ALBUM + " INTEGER," + YEAR + " TEXT," +
            GENRE + " INTEGER," + COVER + " TEXT,"+ UPDATED
            + " DATETIME" + ")";

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALBUMS);
        db.execSQL(CREATE_TABLE_ARTISTS);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_LIBRARY);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIBRARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);

        onCreate(db);
    }

    public long createLibraryEntry(String file) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FILE, file);
        return db.insert(TABLE_LIBRARY, null, values);
    }
}
