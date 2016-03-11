package com.kelsos.mbrc.mappers;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.dao.views.AlbumModelView;
import com.kelsos.mbrc.domain.Album;
import java.util.List;
import rx.Observable;

public class AlbumMapper {
  @NonNull public static Album map(AlbumModelView dao) {
    return new Album(dao.getId(), dao.getName(), dao.getArtist(), dao.getCover());
  }

  public static List<Album> map(List<AlbumModelView> daoList) {
    return Observable.from(daoList).map(AlbumMapper::map).toList().toBlocking().first();
  }
}
