package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.ModelView;
import com.raizlabs.android.dbflow.annotation.ModelViewQuery;
import com.raizlabs.android.dbflow.sql.Query;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModelView;

@ModelView(database = RemoteDatabase.class, name = "track_view")
public class TrackModelView extends BaseModelView<TrackDao> {
  @ModelViewQuery public static final Query QUERY = SQLite.select().from(TrackDao.class);
}
