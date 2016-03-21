package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.GenreDao;
import java.util.List;
import rx.Observable;

public class MapperUtils {
  private MapperUtils() {
    //no instance
  }

  static CoverDao getCoverById(int id, List<CoverDao> data) {
    return Observable.from(data).filter(o -> o.getId() == id).toBlocking().firstOrDefault(null);
  }

  static ArtistDao getArtistById(int id, List<ArtistDao> data) {
    return Observable.from(data).filter(o -> o.getId() == id).toBlocking().firstOrDefault(null);
  }

  static AlbumDao getAlbumById(int id, List<AlbumDao> data) {
    return Observable.from(data).filter(o -> o.getId() == id).toBlocking().firstOrDefault(null);
  }

  static GenreDao getGenreById(int id, List<GenreDao> data) {
    return Observable.from(data).filter(o -> o.getId() == id).toBlocking().firstOrDefault(null);
  }
}
