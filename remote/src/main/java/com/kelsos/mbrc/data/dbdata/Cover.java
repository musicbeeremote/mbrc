package com.kelsos.mbrc.data.dbdata;

import android.content.UriMatcher;
import android.net.Uri;
import com.kelsos.mbrc.data.db.LibraryProvider;

public class Cover {
    public static final int BASE_IMAGE_CODE = 0x92358;
    private static final String TABLE_NAME = "covers";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(LibraryProvider.AUTHORITY_URI, TABLE_NAME);
    public static final Uri CONTENT_IMAGE_URI = Uri.withAppendedPath(CONTENT_URI, "image");
    private String albumId;
    private String coverHash;

    public Cover(String albumId, String coverHash) {
        this.albumId = albumId;
        this.coverHash = coverHash;
    }

    public static Uri getContentUri() {
        return Uri.withAppendedPath(Uri.parse(LibraryProvider.SCHEME + LibraryProvider.AUTHORITY), TABLE_NAME);
    }

    public static void addMatcherUris(UriMatcher uriMatcher) {
        uriMatcher.addURI(LibraryProvider.AUTHORITY, TABLE_NAME + "/image/*", BASE_IMAGE_CODE);
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getCoverHash() {
        return coverHash;
    }

    public void setCoverHash(String coverHash) {
        this.coverHash = coverHash;
    }
}



