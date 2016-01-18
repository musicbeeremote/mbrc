package com.kelsos.mbrc.repository;

import com.annimon.stream.Stream;
import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumDao_Table;
import com.kelsos.mbrc.dao.AlbumModelView;
import com.kelsos.mbrc.dao.AlbumModelView_ViewTable;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistDao_Table;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.CoverDao_Table;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.GenreDao_Table;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dao.TrackModelView;
import com.kelsos.mbrc.dao.TrackModelView_ViewTable;
import com.kelsos.mbrc.dto.library.AlbumDto;
import com.kelsos.mbrc.dto.library.TrackDto;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import rx.Observable;

public class LibraryRepositoryImpl implements LibraryRepository {

  @Override public Observable<List<AlbumModelView>> getAlbums(int offset, int limit) {
    return Observable.defer(() -> Observable.just(SQLite.select()
        .from(AlbumModelView.class)
        .where()
        .limit(limit)
        .offset(offset)
        .queryList()));
  }

  @Override public Observable<List<GenreDao>> getGenres(int offset, int limit) {
    return Observable.defer(() -> Observable.just(SQLite.select()
        .from(GenreDao.class)
        .where()
        .offset(offset)
        .orderBy(OrderBy.fromProperty(GenreDao_Table.name).ascending())
        .limit(limit)
        .queryList()));
  }

  @Override public Observable<List<ArtistDao>> getArtists(int offset, int limit) {
    return Observable.defer(() -> Observable.just(SQLite.select()
        .from(ArtistDao.class)
        .where()
        .offset(offset)
        .orderBy(OrderBy.fromProperty(ArtistDao_Table.name).ascending())
        .limit(limit)
        .queryList()));
  }

  @Override public Observable<List<CoverDao>> getCovers() {
    return Observable.defer(() -> Observable.just(SQLite.select().from(CoverDao.class).queryList()));
  }

  @Override public void saveGenres(List<GenreDao> objects) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(objects).forEach(BaseModel::save);
    });
  }

  @Override public void saveArtists(List<ArtistDao> objects) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(objects).forEach(BaseModel::save);
    });
  }

  @Override public void saveTracks(List<TrackDao> objects) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(objects).forEach(BaseModel::save);
    });
  }

  @Override public void saveAlbums(List<AlbumDao> objects) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(objects).forEach(BaseModel::save);
    });
  }

  @Override public void saveRemoteTracks(List<TrackDto> data) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(data).forEach(value -> {
        TrackDao dao = new TrackDao();
        dao.setId(value.getId());
        dao.setPath(value.getPath());
        dao.setPosition(value.getPosition());
        dao.setTitle(value.getTitle());
        dao.setDisc(value.getDisc());
        dao.setYear(value.getYear());
        dao.setDateAdded(value.getDateAdded());
        dao.setDateDeleted(value.getDateDeleted());
        dao.setDateUpdated(value.getDateUpdated());
        dao.setGenre(getGenreById(value.getGenreId()));
        dao.setAlbumArtist(getArtistById(value.getAlbumArtistId()));
        dao.setArtist(getArtistById(value.getArtistId()));
        dao.setAlbum(getAlbumById(value.getAlbumId()));
        dao.save();
      });
    });
  }

  private GenreDao getGenreById(int genreId) {
    return SQLite.select().from(GenreDao.class).where(GenreDao_Table.id.is(genreId)).querySingle();
  }

  @Override public ArtistDao getArtistById(int artistId) {
    return SQLite.select().from(ArtistDao.class).where(ArtistDao_Table.id.is(artistId)).querySingle();
  }

  @Override public AlbumDao getAlbumById(int albumId) {
    return SQLite.select().from(AlbumDao.class).where(AlbumDao_Table.id.is(albumId)).querySingle();
  }

  @Override public AlbumModelView getAlbumViewById(int albumId) {
    return SQLite.select().from(AlbumModelView.class).where(AlbumModelView_ViewTable.id.is(albumId)).querySingle();
  }

  @Override public void saveRemoteAlbums(List<AlbumDto> data) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(data).forEach(value -> {
        AlbumDao dao = new AlbumDao();
        dao.setId(value.getId());
        dao.setDateAdded(value.getDateAdded());
        dao.setDateDeleted(value.getDateDeleted());
        dao.setDateUpdated(value.getDateUpdated());
        dao.setArtist(getArtistById(value.getArtistId()));
        dao.setCover(getCoverById(value.getCoverId()));
        dao.setName(value.getName());
        dao.save();
      });
    });
  }

  @Override public CoverDao getCoverById(int coverId) {
    return SQLite.select().from(CoverDao.class).where(CoverDao_Table.id.is(coverId)).querySingle();
  }

  @Override public void saveCovers(List<CoverDao> objects) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(objects).forEach(CoverDao::save);
    });
  }

  @Override public Observable<List<TrackModelView>> getTracks(int offset, int limit) {
    return Observable.defer(() -> Observable.just(SQLite.select()
        .from(TrackModelView.class)
        .where()
        .limit(limit)
        .offset(offset)
        .queryList()));
  }

  @Override public Observable<List<TrackModelView>> getTracksByAlbumId(long albumId) {
    return Observable.just(SQLite.select()
        .from(TrackModelView.class)
        .where(TrackModelView_ViewTable.album_id.is(albumId))
        .queryList());
  }
}
