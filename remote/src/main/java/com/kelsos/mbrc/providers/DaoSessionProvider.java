package com.kelsos.mbrc.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.dao.DaoMaster;
import com.kelsos.mbrc.dao.DaoSession;

public class DaoSessionProvider implements Provider<DaoSession> {
    @Inject
    private Context mContext;

    @Override
    public DaoSession get() {
        final DaoMaster daoMaster;
        SQLiteDatabase db;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, "lib-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }
}
