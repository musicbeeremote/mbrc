package com.kelsos.mbrc.interactors.playlists;

import com.kelsos.mbrc.domain.PlaylistTrack;
import java.util.List;
import rx.Observable;

public interface PlaylistTrackInteractor {
  Observable<List<PlaylistTrack>> execute(long playlistId);
}
