package com.kelsos.mbrc.data.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.google.inject.Inject;
import com.kelsos.mbrc.dao.DaoSession;
import com.kelsos.mbrc.dao.QueueTrackDao;
import de.greenrobot.dao.DaoLog;
import roboguice.content.RoboContentProvider;

public class LibraryProvider extends RoboContentProvider {

    public static final String AUTHORITY = "com.kelsos.mbrc.provider";
    public static final String QUEUE_TRACK_BASE_PATH = "nowplaying";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + QUEUE_TRACK_BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + QUEUE_TRACK_BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + QUEUE_TRACK_BASE_PATH;
    public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");

    private static final String QUEUE_TABLENAME = QueueTrackDao.TABLENAME;
    private static final String QUEUE_PK = QueueTrackDao.Properties.Id.columnName;
    private static final String QUEUE_TITLE = QueueTrackDao.Properties.Title.columnName;
    private static final String QUEUE_ARTIST = QueueTrackDao.Properties.Artist.columnName;

    private static final int QUEUETRACK_DIR = 0;
    private static final int QUEUETRACK_ID = 1;
    private static final int QUEUETRACK_FILTER = 2;

    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, QUEUE_TRACK_BASE_PATH, QUEUETRACK_DIR);
        sURIMatcher.addURI(AUTHORITY, QUEUE_TRACK_BASE_PATH + "/#", QUEUETRACK_ID);
        sURIMatcher.addURI(AUTHORITY, QUEUE_TRACK_BASE_PATH + "/filter/*", QUEUETRACK_FILTER);
    }

    @Inject
    private DaoSession daoSession;

    @Override
    public boolean onCreate() {
        super.onCreate();
        // if(daoSession == null) {
        // throw new IllegalStateException("DaoSession must be set before content provider is created");
        // }
        DaoLog.d("Content Provider started: " + CONTENT_URI);
        return true;
    }

    protected SQLiteDatabase getDatabase() {
        if (daoSession == null) {
            throw new IllegalStateException("DaoSession must be set during content provider is active");
        }
        return daoSession.getDatabase();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id;
        String path;
        switch (uriType) {
            case QUEUETRACK_DIR:
                id = getDatabase().insert(QUEUE_TABLENAME, null, values);
                path = QUEUE_TRACK_BASE_PATH + "/" + id;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = getDatabase();
        int rowsDeleted;
        String id;
        switch (uriType) {
            case QUEUETRACK_DIR:
                rowsDeleted = db.delete(QUEUE_TABLENAME, selection, selectionArgs);
                break;
            case QUEUETRACK_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(QUEUE_TABLENAME, QUEUE_PK + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(QUEUE_TABLENAME, QUEUE_PK + "=" + id + " and "
                            + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = getDatabase();
        int rowsUpdated;
        String id;
        switch (uriType) {
            case QUEUETRACK_DIR:
                rowsUpdated = db.update(QUEUE_TABLENAME, values, selection, selectionArgs);
                break;
            case QUEUETRACK_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(QUEUE_TABLENAME, values, QUEUE_PK + "=" + id, null);
                } else {
                    rowsUpdated = db.update(QUEUE_TABLENAME, values, QUEUE_PK + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case QUEUETRACK_DIR:
                queryBuilder.setTables(QUEUE_TABLENAME);
                break;
            case QUEUETRACK_ID:
                queryBuilder.setTables(QUEUE_TABLENAME);
                queryBuilder.appendWhere(QUEUE_PK + "="
                        + uri.getLastPathSegment());
                break;
            case QUEUETRACK_FILTER:
                queryBuilder.setTables(QUEUE_TABLENAME);
                queryBuilder.appendWhere(String.format("%s LIKE '%%%s%%' OR %s LIKE '%%%s%%'",
                        QUEUE_ARTIST,
                        uri.getLastPathSegment(),
                        QUEUE_TITLE,
                        uri.getLastPathSegment()
                ));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = getDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public final String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case QUEUETRACK_DIR:
                return CONTENT_TYPE;
            case QUEUETRACK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
