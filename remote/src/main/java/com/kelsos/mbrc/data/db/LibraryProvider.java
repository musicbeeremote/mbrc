package com.kelsos.mbrc.data.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private final Map<Integer, String> types;

    static {
        Album.addMatcherUris(URI_MATCHER);
        Artist.addMatcherUris(URI_MATCHER);
        Cover.addMatcherUris(URI_MATCHER);
        Genre.addMatcherUris(URI_MATCHER);
        Track.addMatcherUris(URI_MATCHER);
        Playlist.addMatcherUris(URI_MATCHER);
        PlaylistTrack.addMatcherUris(URI_MATCHER);
    }

    private LibraryDbHelper dbHelper;

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
        types.put(Playlist.BASE_ITEM_CODE, Track.TYPE_ITEM);
        types.put(Playlist.BASE_URI_CODE, Track.TYPE_DIR);
        types.put(PlaylistTrack.BASE_ITEM_CODE, PlaylistTrack.TYPE_ITEM);
        types.put(PlaylistTrack.BASE_URI_CODE, PlaylistTrack.TYPE_DIR);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new LibraryDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
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
                        new String[]{search},
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
            case Playlist.BASE_URI_CODE:
                result = dbHelper.getAllPlaylistsCursor(null);
                break;
            case PlaylistTrack.BASE_URI_CODE:
                result = null;
                break;
            case PlaylistTrack.BASE_ITEM_CODE:
                result = null;
                break;
            case PlaylistTrack.BASE_HASH_CODE:
                result = getPlaylistTracks(uri, contentResolver);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown Uri %s", uri));
        }
        return result;
    }

    private Cursor getPlaylistTracks(Uri uri, ContentResolver contentResolver) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result = null;
        if (db != null) {
            String hash = uri.getLastPathSegment();
            sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(String.format("%s pl, %s pltr",
                    Playlist.TABLE_NAME, PlaylistTrack.TABLE_NAME));

            dataSel = String.format("pltr.%s = pl.%s and pl.%s = ?",
                    PlaylistTrack.PLAYLIST_ID, Playlist._ID,
                    Playlist.PLAYLIST_HASH);

            result = sqBuilder.query(db,
                    new String[]{
                            String.format("pltr.%s", PlaylistTrack._ID),
                            PlaylistTrack.PLAYLIST_ID,
                            PlaylistTrack.ARTIST,
                            PlaylistTrack.TITLE,
                            PlaylistTrack.HASH,
                            PlaylistTrack.INDEX,
                    },
                    dataSel,
                    new String[]{hash},
                    String.format("pltr.%s", PlaylistTrack._ID),
                    null,
                    String.format("%s ASC", PlaylistTrack.INDEX)
            );

            if (result != null) {
                result.setNotificationUri(contentResolver, uri);
            }
        }

        return result;
    }

    private Cursor getTracksForAlbumCursor(Uri uri, ContentResolver contentResolver) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result = null;
        if (db != null) {
            String albumId = uri.getLastPathSegment();
            sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(Track.TABLE_NAME);
            dataSel = Track.ALBUM_ID + " = " + " ?";
            result = sqBuilder.query(db,
                    new String[]{
                            Track._ID,
                            Track.TITLE,
                            Track.TRACK_NO
                    },
                    dataSel,
                    new String[]{albumId},
                    Track._ID,
                    null,
                    Track.TRACK_NO + " ASC"
            );
            if (result != null) {
                result.setNotificationUri(contentResolver, uri);
            }
        }
        return result;
    }

    private Cursor getArtistsForGenreCursor(Uri uri, ContentResolver contentResolver) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result = null;
        if (db != null) {
            String genreId = uri.getLastPathSegment();
            sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(String.format("%s ar, %s gen, %s t",
                    Artist.TABLE_NAME, Genre.TABLE_NAME, Track.TABLE_NAME));
            dataSel = "ar." + Artist._ID + " = " + "t." + Track.ARTIST_ID
                    + " and " + "t. " + Track.GENRE_ID + " = " + " gen." + Genre._ID
                    + " and " + " gen." + Genre._ID + " = " + "?";

            result = sqBuilder.query(db,
                    new String[]{Artist.ARTIST_NAME, "ar." + Artist._ID},
                    dataSel,
                    new String[]{genreId},
                    String.format("ar.%s", Artist._ID),
                    null,
                    String.format("%s ASC", Artist.ARTIST_NAME)
            );
            if (result != null) {
                result.setNotificationUri(contentResolver, uri);
            }
        }
        return result;
    }

    private Cursor getAlbumsForArtistCursor(Uri uri, ContentResolver contentResolver) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result = null;
        if (db != null) {
            String artistId = uri.getLastPathSegment();
            sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(String.format("%s al, %s ar", Album.TABLE_NAME, Artist.TABLE_NAME));
            dataSel = "ar." + Artist._ID + " = " + "al." + Album.ARTIST_ID + " and "
                    + "al." + Album.ARTIST_ID + " = " + "?";

            result = sqBuilder.query(db,
                    new String[]{"al." + Album.ALBUM_NAME,
                            "al." + Album._ID,
                            "ar." + Artist.ARTIST_NAME
                    },
                    dataSel,
                    new String[]{artistId},
                    "al." + Album._ID,
                    null,
                    "al." + Album.ALBUM_NAME + " ASC"
            );
            if (result != null) {
                result.setNotificationUri(contentResolver, uri);
            }
        }
        return result;
    }

    private Cursor getAlbumsCursor(Uri uri, ContentResolver contentResolver) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        String dataSel;
        Cursor result = null;
        if (db != null) {
            SQLiteQueryBuilder sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(String.format("%s al, %s ar, %s t",
                    Album.TABLE_NAME, Artist.TABLE_NAME, Track.TABLE_NAME));
            dataSel = "t." + Track.ALBUM_ID + " = " + "al." + Album._ID
                    + " and " + "al." + Album.ARTIST_ID + " = " + "ar." + Artist._ID;


            result = sqBuilder.query(db,
                    new String[]{"al." + Album._ID,
                            Album.ALBUM_NAME,
                            "al." + Album.ARTIST_ID,
                            Artist.ARTIST_NAME},
                    dataSel,
                    null,
                    "al." + Album._ID,
                    null,
                    "ar." + Artist.ARTIST_NAME + ", al." + Album.ALBUM_NAME + " ASC"
            );

            if (result != null) {
                result.setNotificationUri(contentResolver, uri);
            }
        }
        return result;
    }

    @Override
    public String getType(Uri uri) {
        String uriType = types.get(URI_MATCHER.match(uri));
        if (uriType == null || uriType.equals("")) {
            throw new IllegalArgumentException(String.format("Invalid uri: %s", uri));
        }
        return uriType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (URI_MATCHER.match(uri) != Cover.BASE_IMAGE_CODE) {
            throw new IllegalArgumentException("Action not supported");
        } else {
            File file = new File(String.format("%s/%s", getContext().getFilesDir(), uri.getLastPathSegment()));
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        }
    }
}
