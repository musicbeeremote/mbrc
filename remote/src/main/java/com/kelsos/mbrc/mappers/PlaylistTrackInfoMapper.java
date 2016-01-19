package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;
import java.util.List;

public class PlaylistTrackInfoMapper {
  public static List<PlaylistTrackInfoDao> map(List<PlaylistTrackInfo> info) {
    return Stream.of(info).map(PlaylistTrackInfoMapper::map).collect(Collectors.toList());
  }

  public static PlaylistTrackInfoDao map(PlaylistTrackInfo object) {
    PlaylistTrackInfoDao dao = new PlaylistTrackInfoDao();
    dao.setId(object.getId());
    dao.setArtist(object.getArtist());
    dao.setTitle(object.getTitle());
    dao.setPath(object.getPath());
    dao.setDateAdded(object.getDateAdded());
    dao.setDateDeleted(object.getDateDeleted());
    dao.setDateUpdated(object.getDateUpdated());
    return dao;
  }
}
