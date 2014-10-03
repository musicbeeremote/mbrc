package com.kelsos.mbrc.data.db;

import android.content.*;
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
    private Context mContext;

    static {
        LibraryAlbum.addMatcherUris(URI_MATCHER);
        LibraryArtist.addMatcherUris(URI_MATCHER);
        LibraryCover.addMatcherUris(URI_MATCHER);
        LibraryGenre.addMatcherUris(URI_MATCHER);
        LibraryTrack.addMatcherUris(URI_MATCHER);
        Playlist.addMatcherUris(URI_MATCHER);
        PlaylistTrack.addMatcherUris(URI_MATCHER);
        QueueTrack.addMatcherUris(URI_MATCHER);
    }



    public LibraryProvider() {
        types = new Hashtable<>();
        //Creates map of types to avoid switch
        types.put(LibraryAlbum.BASE_ITEM_CODE, LibraryAlbum.TYPE_ITEM);
        types.put(LibraryAlbum.BASE_URI_CODE, LibraryAlbum.TYPE_DIR);
        types.put(LibraryArtist.BASE_ITEM_CODE, LibraryArtist.TYPE_ITEM);
        types.put(LibraryArtist.BASE_URI_CODE, LibraryArtist.TYPE_DIR);
        types.put(LibraryGenre.BASE_ITEM_CODE, LibraryGenre.CONTENT_ITEM_TYPE);
        types.put(LibraryGenre.BASE_URI_CODE, LibraryGenre.CONTENT_TYPE);
        types.put(LibraryGenre.BASE_FILTER_CODE, LibraryGenre.CONTENT_TYPE);
        types.put(LibraryTrack.BASE_ITEM_CODE, LibraryTrack.TYPE_ITEM);
        types.put(LibraryTrack.BASE_URI_CODE, LibraryTrack.TYPE_DIR);
        types.put(Playlist.BASE_ITEM_CODE, LibraryTrack.TYPE_ITEM);
        types.put(Playlist.BASE_URI_CODE, LibraryTrack.TYPE_DIR);
        types.put(PlaylistTrack.BASE_ITEM_CODE, PlaylistTrack.TYPE_ITEM);
        types.put(PlaylistTrack.BASE_URI_CODE, PlaylistTrack.TYPE_DIR);
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        final long id;
        final ContentResolver contentResolver = mContext.getContentResolver();

        switch (URI_MATCHER.match(uri)) {
            case LibraryAlbum.BASE_ITEM_CODE:
                result = getAlbumCursor(uri);
                break;
            case LibraryAlbum.BASE_URI_CODE:
                result = getAlbumsCursor(uri, contentResolver);
                break;
            case LibraryAlbum.BASE_ARTIST_FILTER:
                result = getAlbumsForArtistCursor(uri, contentResolver);
                break;
            case LibraryArtist.BASE_ITEM_CODE:
                break;
            case LibraryArtist.BASE_URI_CODE:
                break;
            case LibraryArtist.BASE_GENRE_FILTER:
                break;
            case LibraryGenre.BASE_ITEM_CODE:

                break;
            case LibraryGenre.BASE_URI_CODE:
                break;
            case LibraryGenre.BASE_FILTER_CODE:

                break;
            case LibraryTrack.BASE_ITEM_CODE:
                break;
            case LibraryTrack.BASE_URI_CODE:
                break;
            case LibraryTrack.BASE_ALBUM_FILTER_CODE:

                break;
            case Playlist.BASE_URI_CODE:
                break;
            case PlaylistTrack.BASE_URI_CODE:
                result = null;
                break;
            case PlaylistTrack.BASE_ITEM_CODE:
                result = null;
                break;
            case PlaylistTrack.BASE_HASH_CODE:

                break;
            case QueueTrack.BASE_URI_CODE:

            default:
                throw new IllegalArgumentException(String.format("Unknown Uri %s", uri));
        }
        return result;
    }

    private Cursor getAlbumCursor(Uri uri) {

        final SQLiteDatabase db = null;
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result = null;
        if (db != null) {
            long id = Long.parseLong(uri.getLastPathSegment());
            sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(String.format("%s al, %s ar, %s t",
                    LibraryAlbum.TABLE_NAME, LibraryArtist.TABLE_NAME, LibraryTrack.TABLE_NAME));
            dataSel = String.format("t.%s = al.%s and al.%s = ar.%s and al.%s = ?",
                    LibraryTrack.ALBUM_ID,
                    LibraryAlbum._ID,
                    LibraryAlbum.ARTIST_ID,
                    LibraryArtist._ID,
                    LibraryAlbum._ID);

            result = sqBuilder.query(db,
                    new String[]{
                            String.format("al.%s", LibraryAlbum._ID),
                            LibraryAlbum.ALBUM_NAME,
                            String.format("al.%s", LibraryAlbum.ARTIST_ID),
                            LibraryArtist.ARTIST_NAME,
                            LibraryAlbum.COVER_HASH
                    },
                    dataSel,
                    new String[] {
                            String.valueOf(id)
                    },
                    String.format("al.%s", LibraryAlbum._ID),
                    null,
                    String.format("ar.%s, al.%s ASC", LibraryArtist.ARTIST_NAME, LibraryAlbum.ALBUM_NAME)
            );
        }
        return result;
    }

    private Cursor getAlbumsForArtistCursor(Uri uri, ContentResolver contentResolver) {
        final SQLiteDatabase db = null;
        SQLiteQueryBuilder sqBuilder;
        String dataSel;
        Cursor result = null;
        if (db != null) {
            String artistId = uri.getLastPathSegment();
            sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(String.format("%s al, %s tr, %s ar", LibraryAlbum.TABLE_NAME,
                    LibraryTrack.TABLE_NAME,
                    LibraryArtist.TABLE_NAME));
            dataSel = String.format("((tr.%s = ? and tr.%s = ar.%s) or (al.%s = ? and al.%s = ar.%s)) and al.%s = tr.%s",
                    LibraryTrack.ARTIST_ID,
                    LibraryTrack.ARTIST_ID,
                    LibraryArtist._ID,
                    LibraryAlbum.ARTIST_ID,
                    LibraryAlbum.ARTIST_ID,
                    LibraryArtist._ID,
                    LibraryAlbum._ID,
                    LibraryTrack.ALBUM_ID);

            result = sqBuilder.query(db,
                    new String[]{
                            String.format("al.%s", LibraryAlbum.ALBUM_NAME),
                            String.format("al.%s", LibraryAlbum._ID),
                            String.format("ar.%s", LibraryArtist.ARTIST_NAME),
                            String.format("al.%s", LibraryAlbum.ARTIST_ID),
                            String.format("al.%s", LibraryAlbum.COVER_HASH)
                    },
                    dataSel,
                    new String[]{artistId, artistId},
                    String.format("al.%s", LibraryAlbum._ID),
                    null,
                    String.format("al.%s ASC", LibraryAlbum.ALBUM_NAME)
            );
            if (result != null) {
                result.setNotificationUri(contentResolver, uri);
            }
        }
        return result;
    }

    private Cursor getAlbumsCursor(Uri uri, ContentResolver contentResolver) {
        final SQLiteDatabase db = null;
        String dataSel;
        Cursor result = null;
        if (db != null) {
            SQLiteQueryBuilder sqBuilder = new SQLiteQueryBuilder();
            sqBuilder.setTables(String.format("%s al, %s ar, %s t",
                    LibraryAlbum.TABLE_NAME, LibraryArtist.TABLE_NAME, LibraryTrack.TABLE_NAME));
            dataSel = String.format("t.%s = al.%s and al.%s = ar.%s",
                    LibraryTrack.ALBUM_ID,
                    LibraryAlbum._ID,
                    LibraryAlbum.ARTIST_ID,
                    LibraryArtist._ID);

            result = sqBuilder.query(db,
                    new String[]{
                            String.format("al.%s", LibraryAlbum._ID),
                            LibraryAlbum.ALBUM_NAME,
                            String.format("al.%s", LibraryAlbum.ARTIST_ID),
                            LibraryArtist.ARTIST_NAME,
                            LibraryAlbum.COVER_HASH
                    },
                    dataSel,
                    null,
                    String.format("al.%s", LibraryAlbum._ID),
                    null,
                    String.format("ar.%s, al.%s ASC", LibraryArtist.ARTIST_NAME, LibraryAlbum.ALBUM_NAME)
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
        if (URI_MATCHER.match(uri) != LibraryCover.BASE_IMAGE_CODE) {
            throw new IllegalArgumentException("Action not supported");
        } else {
            File file = new File(String.format("%s/%s", mContext.getFilesDir(), uri.getLastPathSegment()));
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        }
    }
}
