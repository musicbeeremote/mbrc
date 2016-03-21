package com.kelsos.mbrc.repository.library;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.GenreDao_Table;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import rx.Observable;

public class GenreRepositoryImpl implements GenreRepository {
  @Override public Observable<List<GenreDao>> getPageObservable(int offset, int limit) {
    return Observable.defer(() -> Observable.just(getPage(offset, limit)));
  }

  @Override public Observable<List<GenreDao>> getAllObservable() {
    return Observable.defer(() -> Observable.just(getAll()));
  }

  @Override public List<GenreDao> getPage(int offset, int limit) {
    return SQLite.select()
        .from(GenreDao.class)
        .where()
        .offset(offset)
        .orderBy(OrderBy.fromProperty(GenreDao_Table.name).ascending())
        .limit(limit)
        .queryList();
  }

  @Override public List<GenreDao> getAll() {
    return SQLite.select()
        .from(GenreDao.class)
        .where()
        .orderBy(OrderBy.fromProperty(GenreDao_Table.name).ascending())
        .queryList();
  }

  @Override public GenreDao getById(long id) {
    return SQLite.select().from(GenreDao.class).where(GenreDao_Table.id.eq(id)).querySingle();
  }

  @Override public void save(List<GenreDao> items) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(items).forEach(BaseModel::save);
    });
  }

  @Override public void save(GenreDao item) {
    item.save();
  }

  @Override public long count() {
    return SQLite.selectCountOf().from(GenreDao.class).count();
  }
}
