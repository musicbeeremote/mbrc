package com.kelsos.mbrc.repository.library;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.TrackDao_Table;
import com.kelsos.mbrc.dao.views.TrackModelView;
import com.kelsos.mbrc.dao.views.TrackModelView_ViewTable;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import rx.Observable;

public class TrackRepositoryImpl implements TrackRepository {
  @Override public Observable<List<TrackDao>> getPageObservable(int offset, int limit) {
    return Observable.defer(() -> Observable.just(getPage(offset, limit)));
  }

  @Override public Observable<List<TrackDao>> getAllObservable() {
    return Observable.defer(() -> Observable.just(getAll()));
  }

  @Override public List<TrackDao> getPage(int offset, int limit) {
    return SQLite.select().from(TrackDao.class).limit(limit).offset(offset).queryList();
  }

  @Override public List<TrackDao> getAll() {
    return SQLite.select().from(TrackDao.class).queryList();
  }

  @Override public TrackDao getById(long id) {
    return SQLite.select().from(TrackDao.class).where(TrackDao_Table.id.eq(id)).querySingle();
  }

  @Override public void save(List<? extends TrackDao> items) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(items).forEach(BaseModel::save);
    });
  }

  @Override public void save(TrackDao item) {
    item.save();
  }

  @Override public long count() {
    return SQLite.selectCountOf().from(TrackDao.class).count();
  }

  @Override public Observable<List<TrackModelView>> getTracksByAlbumId(long albumId) {
    return Observable.just(SQLite.select()
        .from(TrackModelView.class)
        .where(TrackModelView_ViewTable.album_id.is(albumId))
        .queryList());
  }

  @Override public Observable<List<TrackModelView>> getTracks(int offset, int limit) {
    return Observable.defer(() -> Observable.just(SQLite.select()
        .from(TrackModelView.class)
        .where()
        .limit(limit)
        .offset(offset)
        .queryList()));
  }
}
