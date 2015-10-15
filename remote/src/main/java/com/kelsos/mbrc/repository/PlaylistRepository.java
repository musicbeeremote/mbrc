package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.playlist.Playlist;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;

import java.util.List;

import rx.Observable;

public interface PlaylistRepository {
  Observable<List<Playlist>> getPlaylists();
  Observable<List<PlaylistTrack>> getPlaylistTracks(long playlistId);
  Observable<List<PlaylistTrackInfo>> getTrackInfo();
}
