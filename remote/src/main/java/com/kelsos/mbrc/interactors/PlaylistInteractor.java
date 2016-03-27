package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.domain.Playlist;
import java.util.List;
import rx.Observable;

public interface PlaylistInteractor {
  Observable<List<Playlist>> getAllPlaylists();

  Observable<List<Playlist>> getUserPlaylists();
}
