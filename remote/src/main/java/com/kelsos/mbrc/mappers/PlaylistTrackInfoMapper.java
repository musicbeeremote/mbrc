package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.dao.PlaylistTrackInfoDao;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;
import java.util.List;
import rx.Observable;

public class PlaylistTrackInfoMapper {
  public static List<PlaylistTrackInfoDao> map(List<PlaylistTrackInfo> info) {
    return Observable.from(info).map(PlaylistTrackInfoMapper::map).toList().toBlocking().first();
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
