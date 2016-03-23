package com.kelsos.mbrc.repository.library;

import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.views.AlbumModelView;
import com.kelsos.mbrc.repository.Repository;
import java.util.List;
import rx.Observable;

public interface AlbumRepository extends Repository<AlbumDao> {
  AlbumModelView getAlbumViewById(int albumId);

  Observable<List<AlbumModelView>> getAlbumViews(int offset, int limit);

  String getAlbumYear(long id);

  Observable<List<AlbumModelView>> getAlbumsByArtist(long artistId);
}
