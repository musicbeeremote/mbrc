package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.dao.PlaylistDao;
import com.kelsos.mbrc.dao.PlaylistTrackDao;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao;
import com.kelsos.mbrc.dao.views.PlaylistTrackView;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.interfaces.ItemProvider;
import java.util.List;
import rx.Observable;

public class PlaylistTrackMapper {
  public static List<PlaylistTrackDao> map(List<PlaylistTrack> data,
      ItemProvider<PlaylistDao> playlistItemProvider,
      ItemProvider<PlaylistTrackInfoDao> infoDaoItemProvider) {
    return Observable.from(data)
        .map(value -> map(value, playlistItemProvider, infoDaoItemProvider))
        .toList().toBlocking().first();
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

  public static com.kelsos.mbrc.domain.PlaylistTrack map(PlaylistTrackView view) {
    com.kelsos.mbrc.domain.PlaylistTrack track = new com.kelsos.mbrc.domain.PlaylistTrack();
    track.setPath(view.getPath());
    track.setPosition(view.getPosition());
    track.setId(view.getId());
    track.setArtist(view.getArtist());
    track.setTitle(view.getTitle());
    return track;
  }
}
