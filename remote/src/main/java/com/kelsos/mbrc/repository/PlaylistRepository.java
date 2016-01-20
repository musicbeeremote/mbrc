package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dao.PlaylistDao;
import com.kelsos.mbrc.dao.PlaylistTrackDao;
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao;
import com.kelsos.mbrc.dao.views.PlaylistTrackView;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;
import java.util.List;
import rx.Observable;

public interface PlaylistRepository {
  Observable<List<Playlist>> getPlaylists();

  void savePlaylists(List<PlaylistDao> playlists);

  Observable<List<PlaylistTrackView>> getPlaylistTracks(long playlistId);

  Observable<List<PlaylistTrackInfo>> getTrackInfo();

  void savePlaylistTrackInfo(List<PlaylistTrackInfoDao> data);

  void savePlaylistTracks(List<PlaylistTrackDao> data);

  PlaylistDao getPlaylistById(long id);

  PlaylistTrackInfoDao getTrackInfoById(long id);
}
