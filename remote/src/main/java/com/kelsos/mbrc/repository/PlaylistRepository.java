package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.Playlist;
import com.kelsos.mbrc.dto.PlaylistTrack;
import com.kelsos.mbrc.dto.PlaylistTrackInfo;

import java.util.List;

import rx.Observable;

public interface PlaylistRepository {
  Observable<List<Playlist>> getPlaylists();
  Observable<List<PlaylistTrack>> getPlaylistTracks(long playlistId);
  Observable<List<PlaylistTrackInfo>> getTrackInfo();
}
