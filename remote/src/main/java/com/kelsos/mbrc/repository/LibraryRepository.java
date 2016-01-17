package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.AlbumModelView;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.GenreDao;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.dto.library.AlbumDto;
import com.kelsos.mbrc.dto.library.TrackDto;
import java.util.List;
import rx.Observable;

public interface LibraryRepository {
  Observable<List<AlbumModelView>> getAlbums(int offset, int limit);

  Observable<List<GenreDao>> getGenres(int offset, int limit);

  Observable<List<ArtistDao>> getArtists(int offset, int limit);

  Observable<List<TrackDao>> getTracks(int offset, int limit);

  Observable<List<CoverDao>> getCovers();

  void saveGenres(List<GenreDao> objects);

  void saveArtists(List<ArtistDao> objects);

  void saveTracks(List<TrackDao> objects);

  void saveAlbums(List<AlbumDao> objects);

  void saveRemoteTracks(List<TrackDto> data);

  ArtistDao getArtistById(int artistId);

  AlbumDao getAlbumById(int albumId);

  AlbumModelView getAlbumViewById(int albumId);

  void saveRemoteAlbums(List<AlbumDto> data);

  CoverDao getCoverById(int coverId);

  void saveCovers(List<CoverDao> map);

  Observable<List<TrackDao>> getTracksByAlbumId(long albumId);
}
