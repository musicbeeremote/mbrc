package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistDao_Table;
import com.kelsos.mbrc.dao.views.GenreArtistView;
import com.kelsos.mbrc.dao.views.GenreArtistView_ViewTable;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import rx.Observable;

public class ArtistRepositoryImpl implements ArtistRepository {
  @Override public Observable<List<GenreArtistView>> getArtistsByGenreId(long id) {
    return Observable.create(subscriber -> {
      List<GenreArtistView> genreArtists = SQLite.select()
          .from(GenreArtistView.class)
          .where(GenreArtistView_ViewTable.genre_id.eq(id))
          .queryList();
      subscriber.onNext(genreArtists);
      subscriber.onCompleted();
    });
  }

  @Override public Observable<List<ArtistDao>> getPage(int offset, int limit) {
    return Observable.create(subscriber -> {
      List<ArtistDao> artists = SQLite.select()
          .from(ArtistDao.class)
          .limit(limit)
          .offset(offset)
          .orderBy(OrderBy.fromProperty(ArtistDao_Table.name))
          .queryList();

      subscriber.onNext(artists);
      subscriber.onCompleted();
    });
  }

  @Override public Observable<List<ArtistDao>> getAll() {
    return Observable.create(subscriber -> {
      List<ArtistDao> artists = SQLite.select()
          .from(ArtistDao.class)
          .orderBy(OrderBy.fromProperty(ArtistDao_Table.name))
          .queryList();

      subscriber.onNext(artists);
      subscriber.onCompleted();
    });
  }

  @Override public ArtistDao getById(long id) {
    return SQLite.select().from(ArtistDao.class).where(ArtistDao_Table.id.is(id)).querySingle();
  }

  @Override public void save(List<ArtistDao> items) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Observable.from(items).forEach(BaseModel::save);
    });
  }

  @Override public void save(ArtistDao item) {
    item.save();
  }

  @Override public long count() {
    return SQLite.selectCountOf().from(ArtistDao.class).count();
  }
}
