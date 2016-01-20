package com.kelsos.mbrc.interactors.playlists;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.PlaylistTrack;
import com.kelsos.mbrc.mappers.PlaylistTrackMapper;
import com.kelsos.mbrc.repository.PlaylistRepository;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class PlaylistTrackInteractorImpl implements PlaylistTrackInteractor {
  @Inject private PlaylistRepository repository;

  @Override public Observable<List<PlaylistTrack>> execute(long playlistId) {
    return repository.getPlaylistTracks(playlistId)
        .flatMap(Observable::from)
        .map(PlaylistTrackMapper::map)
        .collect(ArrayList::new, List::add);
  }
}
