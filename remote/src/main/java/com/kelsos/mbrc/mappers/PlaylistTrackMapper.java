package com.kelsos.mbrc.mappers;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.kelsos.mbrc.dao.PlaylistDao;
import com.kelsos.mbrc.dao.PlaylistTrackDao;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.interfaces.ItemProvider;
import java.util.List;

public class PlaylistTrackMapper {
  public static List<PlaylistTrackDao> map(List<PlaylistTrack> data,
      ItemProvider<PlaylistDao> playlistItemProvider,
      ItemProvider<PlaylistTrackInfoDao> infoDaoItemProvider) {
    return Stream.of(data)
        .map(value -> map(value, playlistItemProvider, infoDaoItemProvider))
        .collect(Collectors.toList());
  }

  public static PlaylistTrackDao map(PlaylistTrack data,
      ItemProvider<PlaylistDao> playlistItemProvider,
      ItemProvider<PlaylistTrackInfoDao> infoDaoItemProvider) {

    PlaylistTrackDao dao = new PlaylistTrackDao();
    dao.setId(data.getId());
    dao.setPlaylist(playlistItemProvider.getById(data.getPlaylistId()));
    dao.setTrackInfo(infoDaoItemProvider.getById(data.getTrackInfoId()));
    dao.setPosition(data.getPosition());
    dao.setDateAdded(data.getDateAdded());
    dao.setDateUpdated(data.getDateUpdated());
    dao.setDateDeleted(data.getDateDeleted());

    return dao;
  }
}
