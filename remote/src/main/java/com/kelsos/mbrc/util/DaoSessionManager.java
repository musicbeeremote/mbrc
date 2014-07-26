package com.kelsos.mbrc.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.dao.DaoMaster;
import com.kelsos.mbrc.dao.DaoSession;


@Singleton
public class DaoSessionManager {

    private DaoSession mDaoSession;

    @Inject
    public DaoSessionManager(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,"com.kelsos.mbrc.dao", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
