package com.kelsos.mbrc.repository;

import com.annimon.stream.Stream;
import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumDao$Table;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.ArtistDao$Table;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dto.library.AlbumDto;
import com.kelsos.mbrc.dto.library.TrackDto;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import rx.Observable;

public class LibraryRepositoryImpl implements LibraryRepository {
  @Override public Observable<List<AlbumDao>> getAlbums(int offset, int limit) {
    return Observable.defer(() -> Observable.just(new Select().from(AlbumDao.class)
        .where()
        .offset(offset)
        .limit(limit)
        .orderBy(true, ArtistDao$Table.NAME, AlbumDao$Table.NAME)
        .queryList()));
  }

  @Override public Observable<List<GenreDao>> getGenres(int offset, int limit) {
    return Observable.defer(() -> Observable.just(new Select().from(GenreDao.class).where()
        .offset(offset)
        .orderBy(ArtistDao$Table.NAME)
        .limit(limit)
        .queryList()));
  }

  @Override public Observable<List<ArtistDao>> getArtists(int offset, int limit) {
    return Observable.defer(() -> Observable.just(new Select().from(ArtistDao.class)
        .where()
        .offset(offset)
        .orderBy(ArtistDao$Table.NAME)
        .limit(limit)
        .queryList()));
  }

  @Override public Observable<List<CoverDao>> getCovers() {
    return Observable.defer(() -> Observable.just(new Select().from(CoverDao.class).queryList()));
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
    return new Select().from(GenreDao.class).byIds(genreId).querySingle();
  }

  @Override public ArtistDao getArtistById(int artistId) {
    return new Select().from(ArtistDao.class).byIds(artistId).querySingle();
  }

  @Override public AlbumDao getAlbumById(int albumId) {
    return new Select().from(AlbumDao.class).byIds(albumId).querySingle();
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
    return new Select().from(CoverDao.class).byIds(coverId).querySingle();
  }

  @Override public void saveCovers(List<CoverDao> objects) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(objects).forEach(CoverDao::save);
    });
  }

  @Override public Observable<List<TrackDao>> getTracks(int offset, int limit) {
    return Observable.defer(() -> Observable.just(new Select().from(TrackDao.class)
        .where()
        .offset(offset)
        .limit(limit)
        .queryList()));
  }
}
