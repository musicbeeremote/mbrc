package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dao.PlaylistDao;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;

import java.util.List;

import rx.Observable;

public interface PlaylistRepository {
  Observable<List<Playlist>> getPlaylists();

  void savePlaylists(List<PlaylistDao> playlists);

  Observable<List<PlaylistTrack>> getPlaylistTracks(long playlistId);
  Observable<List<PlaylistTrackInfo>> getTrackInfo();
}
