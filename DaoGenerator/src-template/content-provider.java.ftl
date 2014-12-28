package ${contentProvider.javaPackage};

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.google.inject.Inject;
import de.greenrobot.dao.DaoLog;
import roboguice.content.RoboContentProvider;

/* Copy this code snippet into your AndroidManifest.xml inside the
<application> element:

<provider
    android:name="${contentProvider.javaPackage}.${contentProvider.className}"
    android:authorities="${contentProvider.authority}"/>
*/

public class ${contentProvider.className} extends RoboContentProvider {

    public static final String AUTHORITY = "${contentProvider.authority}";
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        <#list schema.entities as entity>
        ${entity.className}Helper.addURI(URI_MATCHER);
        </#list>
    }

    /**
    * This must be set from outside, it's recommended to do this inside your Application object.
    * Subject to change (static isn't nice).
    */
    @Inject
    private DaoSession daoSession;

    @Override
    public boolean onCreate() {
        DaoLog.d("Content Provider started: " + AUTHORITY);
        return super.onCreate();
    }

    protected SQLiteDatabase getDatabase() {
        if (daoSession == null) {
            throw new IllegalStateException("DaoSession must be set during content provider is active");
        }
        return daoSession.getDatabase();
    }
    <#--
    ##########################################
    ########## Insert ##############
    ##########################################
    -->
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        <#if contentProvider.isReadOnly()>
            throw new UnsupportedOperationException("This content provider is readonly");
        <#else>
        int uriType = URI_MATCHER.match(uri);
        long id;
        String path;
        switch (uriType) {
        <#list schema.entities as entity>
            case ${entity.className}Helper.${entity.className?upper_case}_DIR:
                id = getDatabase().insert(${entity.className}Helper.TABLENAME, null, values);
                path = ${entity.className}Helper.BASE_PATH + "/" + id;
                break;
        </#list>
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.parse(path);
        </#if>
        }
    <#--
    ##########################################
    ########## Delete ##############
    ##########################################
    -->

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
        <#if contentProvider.isReadOnly()>
            throw new UnsupportedOperationException("This content provider is readonly");
        <#else>
            int uriType = URI_MATCHER.match(uri);
            SQLiteDatabase db = getDatabase();
            int rowsDeleted;
            String id;
            switch (uriType) {
            <#list schema.entities as entity>
                case ${entity.className}Helper.${entity.className?upper_case}_DIR:
                    rowsDeleted = db.delete(${entity.className}Helper.TABLENAME, selection, selectionArgs);
                    break;
                case ${entity.className}Helper.${entity.className?upper_case}_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(${entity.className}Helper.TABLENAME,
                            ${entity.className}Helper.PK + "=" + id, null);
                    } else {
                        rowsDeleted = db.delete(${entity.className}Helper.TABLENAME,
                            ${entity.className}Helper.PK + "=" + id + " and " + selection, selectionArgs);
                    }
                    break;
            </#list>
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return rowsDeleted;
        </#if>
        }

    <#--
    ##########################################
    ########## Update ##############
    ##########################################
    -->
    @Override
    public int update(Uri uri, ContentValues values, String selection,
        String[] selectionArgs) {
        <#if contentProvider.isReadOnly()>
            throw new UnsupportedOperationException("This content provider is readonly");
        <#else>
            int uriType = URI_MATCHER.match(uri);
            SQLiteDatabase db = getDatabase();
            int rowsUpdated;
            String id;
            switch (uriType) {
            <#list schema.entities as entity>
                case ${entity.className}Helper.${entity.className?upper_case}_DIR:
                    rowsUpdated = db.update(${entity.className}Helper.TABLENAME, values, selection, selectionArgs);
                    break;
                case ${entity.className}Helper.${entity.className?upper_case}_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(${entity.className}Helper.TABLENAME,
                            values, ${entity.className}Helper.PK + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(${entity.className}Helper.TABLENAME,
                            values, ${entity.className}Helper.PK + "=" + id + " and "
                            + selection, selectionArgs);
                    }
                    break;
            </#list>

                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return rowsUpdated;
        </#if>
    }
    <#--
    ##########################################
    ########## Query ##############
    ##########################################
    -->
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
        <#list schema.entities as entity>
            case ${entity.className}Helper.${entity.className?upper_case}_DIR:
                queryBuilder.setTables(${entity.className}Helper.TABLENAME);
                break;
            case ${entity.className}Helper.${entity.className?upper_case}_ID:
                queryBuilder.setTables(${entity.className}Helper.TABLENAME);
                queryBuilder.appendWhere(${entity.className}Helper.PK + "=" + uri.getLastPathSegment());
                break;
        </#list>
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = getDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
        selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    <#--
    ##########################################
    ########## GetType ##############
    ##########################################
    -->
    @Override
    public final String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
        <#list schema.entities as entity>
            case ${entity.className}Helper.${entity.className?upper_case}_DIR:
                return ${entity.className}Helper.CONTENT_TYPE;
            case ${entity.className}Helper.${entity.className?upper_case}_ID:
                return ${entity.className}Helper.CONTENT_ITEM_TYPE;
        </#list>
            default :
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
