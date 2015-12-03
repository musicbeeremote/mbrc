package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.playlist.PlaylistDto;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo;

import java.util.List;

import rx.Observable;

public interface PlaylistRepository {
  Observable<List<PlaylistDto>> getPlaylists();
  Observable<List<PlaylistTrack>> getPlaylistTracks(long playlistId);
  Observable<List<PlaylistTrackInfo>> getTrackInfo();
}
