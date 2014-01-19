package com.kelsos.mbrc.data.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import com.kelsos.mbrc.data.dbdata.*;

import java.io.File;
import java.io.FileNotFoundException;

public class LibraryProvider extends ContentProvider {
    public static final String AUTHORITY = "com.kelsos.mbrc.provider";
    public static final String SCHEME = "content://";
    public static final Uri AUTHORITY_URI = Uri.parse(SCHEME + AUTHORITY);
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private LibraryDbHelper dbHelper;

    static {
        Album.addMatcherUris(URI_MATCHER);
        Artist.addMatcherUris(URI_MATCHER);
        Cover.addMatcherUris(URI_MATCHER);
        Genre.addMatcherUris(URI_MATCHER);
        Track.addMatcherUris(URI_MATCHER);
    }
    @Override public boolean onCreate() {
        dbHelper = new LibraryDbHelper(getContext());
        return false;
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result;
        final long id;
        ContentResolver contentResolver = getContext().getContentResolver();
        String dataSel;
        switch (URI_MATCHER.match(uri)) {
            case Album.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getAlbumCursor(id);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Album.BASE_URI_CODE:
                SQLiteQueryBuilder sqBuilder = new SQLiteQueryBuilder();
                sqBuilder.setTables(String.format("%s al, %s ar, %s t",
                        Album.TABLE_NAME, Artist.TABLE_NAME, Track.TABLE_NAME));
                dataSel = "t." + Track.ALBUM_ID + " = " + "al."+ Album._ID
                        + " and " + "al." + Album.ARTIST_ID + " = " + "ar." + Artist._ID;

               result = sqBuilder.query(dbHelper.getReadableDatabase(),
                        new String[] {"al." + Album._ID,
                                Album.ALBUM_NAME,
                                "al." + Album.ARTIST_ID,
                                Artist.ARTIST_NAME},
                        dataSel,
                        null,
                       "al." + Album._ID,
                        null,
                        "ar."+ Artist.ARTIST_NAME + ", al." + Album.ALBUM_NAME + " ASC");

                result.setNotificationUri(contentResolver, uri);
                break;
            case Album.BASE_ARTIST_FILTER:
                String artistId = uri.getLastPathSegment();
                sqBuilder = new SQLiteQueryBuilder();
                sqBuilder.setTables(String.format("%s al, %s ar", Album.TABLE_NAME, Artist.TABLE_NAME));
                dataSel = "ar." + Artist._ID + " = " + "al." + Album.ARTIST_ID + " and "
                        + "al." + Album.ARTIST_ID + " = " + "?";
                result = sqBuilder.query(dbHelper.getReadableDatabase(),
                        new String[]{"al." + Album.ALBUM_NAME,
                                "al." + Album._ID,
                                "ar." + Artist.ARTIST_NAME
                        },
                        dataSel,
                        new String[]{artistId},
                        "al." + Album._ID,
                        null,
                        "al." + Album.ALBUM_NAME + " ASC");
                result.setNotificationUri(contentResolver, uri);
                break;
            case Artist.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getArtistCursor(id);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Artist.BASE_URI_CODE:
                result = dbHelper.getAllArtistsCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Artist.BASE_GENRE_FILTER:
                String genreId = uri.getLastPathSegment();
                sqBuilder = new SQLiteQueryBuilder();
                sqBuilder.setTables(String.format("%s ar, %s gen, %s t",
                        Artist.TABLE_NAME, Genre.TABLE_NAME, Track.TABLE_NAME));
                dataSel = "ar." + Artist._ID + " = " + "t." + Track.ARTIST_ID
                        + " and " + "t. " + Track.GENRE_ID + " = " + " gen." + Genre._ID
                        + " and " + " gen." + Genre._ID + " = " + "?";
                result = sqBuilder.query(dbHelper.getReadableDatabase(),
                        new String[] {Artist.ARTIST_NAME, "ar." + Artist._ID},
                        dataSel,
                        new String[] {genreId},
                        "ar." + Artist._ID,
                        null,
                        Artist.ARTIST_NAME + " ASC");
                result.setNotificationUri(contentResolver, uri);
                break;
            case Cover.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getCoverCursor(id);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Cover.BASE_URI_CODE:
                result = dbHelper.getAllCoversCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Genre.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getGenreCursor(id);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Genre.BASE_URI_CODE:
                result = dbHelper.getAllGenresCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Genre.BASE_FILTER_CODE:
                String search = "%" + uri.getLastPathSegment() + "%";
                result = dbHelper.getAllGenresCursor(Genre.GENRE_NAME + " LIKE ?",
                        new String[] {search},
                        sortOrder);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Track.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getTrackCursor(id);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Track.BASE_URI_CODE:
                result = dbHelper.getAllTracksCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Track.BASE_ALBUM_FILTER_CODE:
                String albumId = uri.getLastPathSegment();
                sqBuilder = new SQLiteQueryBuilder();
                sqBuilder.setTables(Track.TABLE_NAME);
                dataSel = Track.ALBUM_ID + " = " + " ?";
                result = sqBuilder.query(dbHelper.getReadableDatabase(),
                        new String[]{
                                Track._ID,
                                Track.TITLE,
                                Track.TRACK_NO
                        },
                        dataSel,
                        new String[]{albumId},
                        Track._ID,
                        null,
                        Track.TRACK_NO + " ASC");
                result.setNotificationUri(contentResolver, uri);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown Uri %s", uri));
        }
        return result;
    }

    @Override public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case Album.BASE_ITEM_CODE:
                return Album.TYPE_ITEM;
            case Album.BASE_URI_CODE:
                return Album.TYPE_DIR;
            case Artist.BASE_ITEM_CODE:
                return Artist.TYPE_ITEM;
            case Artist.BASE_URI_CODE:
                return Artist.TYPE_DIR;
            case Cover.BASE_ITEM_CODE:
                return Cover.CONTENT_ITEM_TYPE;
            case Cover.BASE_URI_CODE:
                return Cover.CONTENT_TYPE;
            case Genre.BASE_ITEM_CODE:
                return Genre.CONTENT_ITEM_TYPE;
            case Genre.BASE_URI_CODE:
                return Genre.CONTENT_TYPE;
            case Genre.BASE_FILTER_CODE:
                return Genre.CONTENT_TYPE;
            case Track.BASE_ITEM_CODE:
                return Track.TYPE_ITEM;
            case Track.BASE_URI_CODE:
                return Track.TYPE_DIR;
            default:
                throw new IllegalArgumentException(String.format("Unknown Uri %s", uri));
            
        }
    }

    @Override public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (URI_MATCHER.match(uri) != Cover.BASE_IMAGE_CODE) {
            throw new IllegalArgumentException("Action not supported");
        } else {
            File file = new File(String.format("%s/%s", getContext().getFilesDir(), uri.getLastPathSegment()));
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        }
    }
}
