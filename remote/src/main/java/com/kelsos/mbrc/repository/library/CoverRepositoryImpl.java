package com.kelsos.mbrc.repository.library;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.TrackDao_Table;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import rx.Observable;

public class CoverRepositoryImpl implements CoverRepository {
  @Override public Observable<List<CoverDao>> getPageObservable(int offset, int limit) {
    return Observable.defer(() -> Observable.just(getPage(offset, limit)));
  }

  @Override public Observable<List<CoverDao>> getAllObservable() {
    return Observable.defer(() -> Observable.just(getAll()));
  }

  @Override public List<CoverDao> getPage(int offset, int limit) {
    return SQLite.select()
        .from(CoverDao.class)
        .limit(limit)
        .offset(offset)
        .queryList();
  }

  @Override public List<CoverDao> getAll() {
    return SQLite.select()
        .from(CoverDao.class)
        .queryList();
  }

  @Override public CoverDao getById(long id) {
    return SQLite.select().from(CoverDao.class).where(TrackDao_Table.id.eq(id)).querySingle();
  }

  @Override public void save(List<CoverDao> items) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(items).forEach(BaseModel::save);
    });
  }

  @Override public void save(CoverDao item) {
    item.save();
  }

  @Override public long count() {
    return SQLite.selectCountOf().from(CoverDao.class).count();
  }
}
