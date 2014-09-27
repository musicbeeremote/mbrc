package com.kelsos.mbrc.data.dbdata;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import com.kelsos.mbrc.data.db.LibraryProvider;

public abstract class DataItem implements BaseColumns {

    public DataItem() { }

    public abstract ContentValues getContentValues();

    public abstract String getTableName();

    public abstract long getId();

    public abstract void setId(final long id);

    public Uri getUri() {
        return Uri.withAppendedPath(getBaseUri(), Long.toString(getId()));
    }

    public Uri getBaseUri() {
        return Uri.withAppendedPath(
                Uri.parse(LibraryProvider.SCHEME
                        + LibraryProvider.AUTHORITY), getTableName());
    }

}
