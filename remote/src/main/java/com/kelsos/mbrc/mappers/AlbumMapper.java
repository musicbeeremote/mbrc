package com.kelsos.mbrc.mappers;

import android.support.annotation.NonNull;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.views.AlbumModelView;
import com.kelsos.mbrc.domain.Album;
import java.util.List;

public class AlbumMapper {
  @NonNull public static Album map(AlbumModelView dao) {
    return new Album(dao.getId(), dao.getName(), dao.getArtist(), dao.getCover());
  }

  public static List<Album> map(List<AlbumModelView> daoList) {
    return Stream.of(daoList).map(AlbumMapper::map).collect(Collectors.toList());
  }
}
