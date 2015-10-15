package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.playlist.Playlist;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;

import java.util.List;

import rx.Observable;

public class PlaylistRepositoryImpl implements PlaylistRepository {
  @Override
  public Observable<List<Playlist>> getPlaylists() {
    return null;
  }

  @Override
  public Observable<List<PlaylistTrack>> getPlaylistTracks(long playlistId) {
    return null;
  }

  @Override
  public Observable<List<PlaylistTrackInfo>> getTrackInfo() {
    return null;
  }
}
