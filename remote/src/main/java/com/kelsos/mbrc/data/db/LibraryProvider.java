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
import java.util.Hashtable;
import java.util.Map;

public class LibraryProvider extends ContentProvider {
    public static final String AUTHORITY = "com.kelsos.mbrc.provider";
    public static final String SCHEME = "content://";
    public static final Uri AUTHORITY_URI = Uri.parse(SCHEME + AUTHORITY);
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private LibraryDbHelper dbHelper;
    private final Map<Integer,String> types;

    static {
        Album.addMatcherUris(URI_MATCHER);
        Artist.addMatcherUris(URI_MATCHER);
        Cover.addMatcherUris(URI_MATCHER);
        Genre.addMatcherUris(URI_MATCHER);
        Track.addMatcherUris(URI_MATCHER);
    }

    public LibraryProvider() {
        types = new Hashtable<>();
        //Creates map of types to avoid switch
        types.put(Album.BASE_ITEM_CODE, Album.TYPE_ITEM);
        types.put(Album.BASE_URI_CODE, Album.TYPE_DIR);
        types.put(Artist.BASE_ITEM_CODE, Artist.TYPE_ITEM);
        types.put(Artist.BASE_URI_CODE, Artist.TYPE_DIR);
        types.put(Cover.BASE_ITEM_CODE, Cover.CONTENT_ITEM_TYPE);
        types.put(Cover.BASE_URI_CODE, Cover.CONTENT_TYPE);
        types.put(Genre.BASE_ITEM_CODE, Genre.CONTENT_ITEM_TYPE);
        types.put(Genre.BASE_URI_CODE, Genre.CONTENT_TYPE);
        types.put(Genre.BASE_FILTER_CODE, Genre.CONTENT_TYPE);
        types.put(Track.BASE_ITEM_CODE, Track.TYPE_ITEM);
        types.put(Track.BASE_URI_CODE, Track.TYPE_DIR);
    }

    @Override public boolean onCreate() {
        dbHelper = new LibraryDbHelper(getContext());
        return false;
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result;
        final long id;
        ContentResolver contentResolver = getContext().getContentResolver();
        switch (URI_MATCHER.match(uri)) {
            case Album.BASE_ITEM_CODE:
                id = Long.parseLong(uri.getLastPathSegment());
                result = dbHelper.getAlbumCursor(id);
                result.setNotificationUri(contentResolver, uri);
                break;
            case Album.BASE_URI_CODE:
                result = getAlbumsCursor(uri, contentResolver);
                break;
            case Album.BASE_ARTIST_FILTER:
                result = getAlbumsForArtistCursor(uri, contentResolver);
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
                result = getArtistsForGenreCursor(uri, contentResolver);
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
                result = getTracksForAlbumCursor(uri, contentResolver);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown Uri %s", uri));
        }
        return result;
    }

    private Cursor getTracksForAlbumCursor(Uri uri, ContentResolver contentResolver) {
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result;
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
        return result;
    }

    private Cursor getArtistsForGenreCursor(Uri uri, ContentResolver contentResolver) {
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result;
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
        return result;
    }

    private Cursor getAlbumsForArtistCursor(Uri uri, ContentResolver contentResolver) {
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result;
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
        return result;
    }

    private Cursor getAlbumsCursor(Uri uri, ContentResolver contentResolver) {
        String dataSel;
        Cursor result;
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
        return result;
    }

    @Override public String getType(Uri uri) {
        String uriType = types.get(URI_MATCHER.match(uri));
        if (uriType == null || uriType.equals("")) {
            throw new IllegalArgumentException(String.format("Invalid uri: %s", uri));
        }
        return uriType;
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
