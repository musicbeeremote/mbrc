package com.kelsos.mbrc.interactors.playlists;

import java.util.List;
import rx.Observable;

/**
 * Interactor responsible for creating playlists and adding tracks to new playlists
 */
public interface PlaylistAddInteractor {
  /**
   * Creates a new playlist.
   *
   * @param name The name of the new playlist.
   * @param tracks A list of the paths for the tracks to be in the playlist.
   * @return An {@link Observable} that will emit the success status of the request and complete.
   */
  Observable<Boolean> createPlaylist(String name, List<String> tracks);

  /**
   * Adds tracks to an existing playlist.
   *
   * @param id The id of the playlist that we want to add tracks to.
   * @param tracks A list of the paths for the tracks to be added in the playlist.
   * @return An {@link Observable} that will emit the success status of the request and complete.
   */
  Observable<Boolean> addToPlaylist(long id, List<String> tracks);
}
