package com.kelsos.mbrc.data.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;

public class LibraryProvider extends ContentProvider {
    public static final String AUTHORITY = "com.kelsos.mbrc.provider";
    public static final String SCHEME = "content://";
    public static final Uri AUTHORITY_URI = Uri.parse(SCHEME + AUTHORITY);

    private Context mContext;

    static {

    }



    public LibraryProvider() {
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }


    @Override
    public String getType(Uri uri) {
        throw new IllegalArgumentException(String.format("Invalid uri: %s", uri));
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
        throw new IllegalArgumentException("Action not supported");
    }
}
