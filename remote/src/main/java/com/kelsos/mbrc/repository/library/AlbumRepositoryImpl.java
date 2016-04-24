package com.kelsos.mbrc.repository.library;

import android.text.TextUtils;
import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumDao_Table;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.TrackDao_Table;
import com.kelsos.mbrc.dao.views.AlbumModelView;
import com.kelsos.mbrc.dao.views.AlbumModelView_ViewTable;
import com.kelsos.mbrc.dao.views.ArtistAlbumView;
import com.kelsos.mbrc.dao.views.ArtistAlbumView_ViewTable;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import rx.Observable;

public class AlbumRepositoryImpl implements AlbumRepository {
  @Override public Observable<List<AlbumDao>> getPageObservable(int offset, int limit) {
    return Observable.defer(() -> Observable.just(getPage(offset, limit)));
  }

  @Override public Observable<List<AlbumDao>> getAllObservable() {
    return Observable.defer(() -> Observable.just(getAll()));
  }

  @Override public List<AlbumDao> getPage(int offset, int limit) {
    return SQLite.select().from(AlbumDao.class).limit(limit).offset(offset).queryList();
  }

  @Override public List<AlbumDao> getAll() {
    return SQLite.select().from(AlbumDao.class).queryList();
  }

  @Override public AlbumDao getById(long id) {
    return SQLite.select().from(AlbumDao.class).where(AlbumDao_Table.id.eq(id)).querySingle();
  }

  @Override public void save(List<? extends AlbumDao> items) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(items).forEach(BaseModel::save);
    });
  }

  @Override public void save(AlbumDao item) {
    item.save();
  }

  @Override public long count() {
    return SQLite.selectCountOf().from(AlbumDao.class).count();
  }

  @Override public AlbumModelView getAlbumViewById(int albumId) {
    return SQLite.select().from(AlbumModelView.class).where(AlbumModelView_ViewTable.id.is(albumId)).querySingle();
  }

  @Override public Observable<List<AlbumModelView>> getAlbumViews(int offset, int limit) {
    return Observable.defer(() -> Observable.just(SQLite.select().from(AlbumModelView.class).queryList()));
  }

  @Override public String getAlbumYear(long id) {
    TrackDao trackDao = SQLite.select(TrackDao_Table.year)
        .distinct()
        .from(TrackDao.class)
        .where(TrackDao_Table.album_id.eq(id))
        .groupBy(TrackDao_Table.year)
        .querySingle();

    if (trackDao == null) {
      return "";
    } else {
      return TextUtils.isEmpty(trackDao.getYear()) ? "" : trackDao.getYear();
    }
  }

  @Override public Observable<List<ArtistAlbumView>> getAlbumsByArtist(long artistId) {
    return Observable.defer(() -> Observable.just(SQLite.select()
        .from(ArtistAlbumView.class)
        .where(ArtistAlbumView_ViewTable.artist_id.eq(artistId))
        .queryList()));
  }
}
