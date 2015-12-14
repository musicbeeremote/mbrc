package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.AlbumDao;
import com.kelsos.mbrc.dao.ArtistDao;
import com.kelsos.mbrc.dao.CoverDao;
import com.kelsos.mbrc.dao.TrackDao;
import com.kelsos.mbrc.domain.Track;
import java.util.List;

public class TrackMapper {
  public static Track map(TrackDao dao) {
    final ArtistDao artist = dao.getArtist();
    final String trackArtist = artist != null ? artist.getName() : "";
    final AlbumDao album = dao.getAlbum();
    String coverHash = "";
    if (album != null) {
      CoverDao cover = album.getCover();
      if (cover != null) {
        coverHash = cover.getHash();
      }
    }
    return new Track(dao.getId(), trackArtist, dao.getTitle(), coverHash);
  }

  public static List<Track> map(List<TrackDao> dao) {
    return Stream.of(dao).map(TrackMapper::map).collect(Collectors.toList());
  }
}
