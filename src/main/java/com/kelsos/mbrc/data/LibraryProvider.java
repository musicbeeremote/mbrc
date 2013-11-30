package com.kelsos.mbrc.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class LibraryProvider extends ContentProvider {
    public static final String AUTHORITY = "com.kelsos.mbrc.provider";
    public static final String SCHEME = "content://";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private LibraryDbHelper dbHelper;

    static {
        Album.addMatcherUris(sUriMatcher);
        Artist.addMatcherUris(sUriMatcher);
        Cover.addMatcherUris(sUriMatcher);
        Genre.addMatcherUris(sUriMatcher);
        Track.addMatcherUris(sUriMatcher);
    }
    @Override public boolean onCreate() {
        dbHelper = new LibraryDbHelper(getContext());
        return false;
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        final long id;
        switch (sUriMatcher.match(uri)) {
            case Album.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getAlbumCursor(id);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Album.BASE_URI_CODE:
                result = dbHelper.getAllAlbumsCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Artist.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getArtistCursor(id);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Artist.BASE_URI_CODE:
                result = dbHelper.getAllArtistsCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Cover.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getCoverCursor(id);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Cover.BASE_URI_CODE:
                result = dbHelper.getAllCoversCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Genre.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getGenreCursor(id);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Genre.BASE_URI_CODE:
                result = dbHelper.getAllGenresCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Track.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getTrackCursor(id);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case Track.BASE_URI_CODE:
                result = dbHelper.getAllTracksCursor(selection, selectionArgs, sortOrder);
                result.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown Uri %s", uri));
        }
        return result;
    }

    @Override public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case Album.BASE_ITEM_CODE:
                return Album.TYPE_ITEM;
            case Album.BASE_URI_CODE:
                return Album.TYPE_DIR;
            case Artist.BASE_ITEM_CODE:
                return Artist.TYPE_ITEM;
            case Artist.BASE_URI_CODE:
                return Artist.TYPE_DIR;
            case Cover.BASE_ITEM_CODE:
                return Cover.TYPE_ITEM;
            case Cover.BASE_URI_CODE:
                return Cover.TYPE_DIR;
            case Genre.BASE_ITEM_CODE:
                return Genre.TYPE_ITEM;
            case Genre.BASE_URI_CODE:
                return Genre.TYPE_DIR;
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
}
