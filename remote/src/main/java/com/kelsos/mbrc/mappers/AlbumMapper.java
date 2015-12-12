package com.kelsos.mbrc.mappers;

import android.support.annotation.NonNull;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.domain.Album;
import java.util.List;

public class AlbumMapper {
  @NonNull public static Album map(AlbumDao dao) {
    final ArtistDao artist = dao.getArtist();
    String artistName = artist != null ? artist.getName() : "";
    final CoverDao cover = dao.getCover();
    String hash = cover != null ? cover.getHash() : "";
    return new Album(dao.getId(), dao.getName(), artistName, hash);
  }

  public static List<Album> map(List<AlbumDao> daoList) {
    return Stream.of(daoList).map(AlbumMapper::map).collect(Collectors.toList());
  }
}
