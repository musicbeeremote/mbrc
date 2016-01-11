package com.kelsos.mbrc.repository;

import com.annimon.stream.Stream;
import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumModelView;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dto.library.AlbumDto;
import com.kelsos.mbrc.dto.library.TrackDto;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import java.util.List;
import roboguice.util.Ln;
import rx.Observable;

public class LibraryRepositoryImpl implements LibraryRepository {

  public static final String ALBUM = "album";

  @Override public Observable<List<AlbumDao>> getAlbums(int offset, int limit) {

    //final String artistName = ArtistDao$Table.TABLE_NAME + "." + ArtistDao$Table.NAME;
    //final String albumName = AlbumDao$Table.TABLE_NAME + "." + AlbumDao$Table.ALBUM_NAME;
    //return Observable.defer(() -> {
    //
    //  //final Where<AlbumDao> albumDaoWhere = new Select(AlbumDao$Table.TABLE_NAME + "." + AlbumDao$Table.ID,
    //  //    AlbumDao$Table.ALBUM_NAME,
    //  //    AlbumDao$Table.ARTIST_ARTIST_ID,
    //  //    AlbumDao$Table.COVER_COVER_ID).from(AlbumDao.class)
    //  //    .join(ArtistDao.class, Join.JoinType.INNER)
    //  //    .on(Condition.column(ArtistDao$Table.TABLE_NAME + "." + ArtistDao$Table.ID)
    //  //        .is(AlbumDao$Table.ARTIST_ARTIST_ID))
    //  //    .where()
    //  //    .offset(offset)
    //  //    .limit(limit)
    //  //    .orderBy(true, artistName, albumName);
    //
    //  return Observable.just(albumDaoWhere.queryList());
    //});

    List<AlbumModelView> modelViews = SQLite.select()
        .from(AlbumModelView.class)
        .where()
        .limit(limit)
        .offset(offset)
        .queryList();

    Ln.v(modelViews.size());

    return Observable.empty();
  }

  @Override public Observable<List<GenreDao>> getGenres(int offset, int limit) {
    //return Observable.defer(() -> Observable.just(new Select().from(GenreDao.class)
    //    .where()
    //    .offset(offset)
    //    .orderBy(ArtistDao_Table.NAME)
    //    .limit(limit)
    //    .queryList()));
    return Observable.empty();
  }


  @Override public Observable<List<ArtistDao>> getArtists(int offset, int limit) {
    //return Observable.defer(() -> Observable.just(new Select().from(ArtistDao.class)
    //    .where()
    //    .offset(offset)
    //    .orderBy(ArtistDao_Table.NAME)
    //    .limit(limit)
    //    .queryList()));
    return Observable.empty();
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
    //return new Select().from(GenreDao.class).byIds(genreId).querySingle();
    return new GenreDao();
  }

  @Override public ArtistDao getArtistById(int artistId) {
    //return new Select().from(ArtistDao.class).byIds(artistId).querySingle();
    return new ArtistDao();
  }

  @Override public AlbumDao getAlbumById(int albumId) {
    //return new Select().from(AlbumDao.class).byIds(albumId).querySingle();
    return new AlbumDao();
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
    //return new Select().from(CoverDao.class).byIds(coverId).querySingle();
    return new CoverDao();
  }

  @Override public void saveCovers(List<CoverDao> objects) {
    TransactionManager.transact(RemoteDatabase.NAME, () -> {
      Stream.of(objects).forEach(CoverDao::save);
    });
  }

  @Override public Observable<List<TrackDao>> getTracks(int offset, int limit) {
    //final String albumName = AlbumDao$Table.TABLE_NAME + "." + AlbumDao$Table.ALBUM_NAME;
    //final String albumArtist = ArtistDao$Table.TABLE_NAME + "." + ArtistDao$Table.NAME;
    //return Observable.defer(() -> {
    //  final Where<TrackDao> where = new Select(TrackDao$Table.TABLE_NAME + "." + TrackDao$Table.ID,
    //      TrackDao$Table.TITLE,
    //      TrackDao$Table.ALBUM_ALBUM_ID,
    //      TrackDao$Table.TABLE_NAME + "." + TrackDao$Table.ARTIST_ARTIST_ID).from(TrackDao.class)
    //      .join(AlbumDao.class, Join.JoinType.INNER)
    //      .on(Condition.column(AlbumDao$Table.TABLE_NAME + "." + AlbumDao$Table.ID).is(TrackDao$Table.ALBUM_ALBUM_ID))
    //      .join(ArtistDao.class, Join.JoinType.INNER)
    //      .on(Condition.column(ArtistDao$Table.TABLE_NAME + "." + ArtistDao$Table.ID)
    //          .is(TrackDao$Table.ALBUMARTIST_ALBUM_ARTIST_ID))
    //      .where()
    //      .offset(offset)
    //      .limit(limit)
    //      .orderBy(true, albumArtist, albumName, TrackDao$Table.POSITION);
    //  return Observable.just(where.queryList());
    //});
    return Observable.empty();
  }

  @Override public Observable<List<TrackDao>> getTracksByAlbumId(long albumId) {
    //return Observable.defer(() -> {
    //  final Where<TrackDao> where = new Select(TrackDao$Table.TABLE_NAME + "." + TrackDao$Table.ID,
    //      TrackDao$Table.TITLE,
    //      TrackDao$Table.ALBUM_ALBUM_ID,
    //      TrackDao$Table.TABLE_NAME + "." + TrackDao$Table.ARTIST_ARTIST_ID).from(TrackDao.class)
    //      .join(ArtistDao.class, Join.JoinType.INNER)
    //      .on(Condition.column(ArtistDao$Table.TABLE_NAME + "." + ArtistDao$Table.ID)
    //          .is(TrackDao$Table.ALBUMARTIST_ALBUM_ARTIST_ID))
    //      .where(Condition.column(TrackDao$Table.ALBUM_ALBUM_ID).is(albumId))
    //      .orderBy(true, TrackDao$Table.POSITION);
    //  return Observable.just(where.queryList());
    //});
    return Observable.empty();
  }
}
