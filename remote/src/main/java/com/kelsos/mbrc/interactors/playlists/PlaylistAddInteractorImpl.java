package com.kelsos.mbrc.interactors.playlists;

import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Code;
import com.kelsos.mbrc.dto.requests.PlaylistRequest;
import com.kelsos.mbrc.services.api.PlaylistService;
import java.util.List;
import rx.Observable;

public class PlaylistAddInteractorImpl implements PlaylistAddInteractor {

  @Inject private PlaylistService service;

  /**
   * Creates a new playlist.
   *
   * @param name The name of the new playlist.
   * @param tracks A list of the paths for the tracks to be in the playlist.
   * @return An {@link Observable} that will emit the success status of the request and complete.
   */
  @Override public Observable<Boolean> createPlaylist(String name, List<String> tracks) {
    final PlaylistRequest request = new PlaylistRequest();
    request.setName(name);
    request.setList(tracks);
    return service.createPlaylist(request)
        .map(baseResponse -> baseResponse.getCode() == Code.SUCCESS);
  }

  /**
   * Adds tracks to an existing playlist.
   *
   * @param id The id of the playlist that we want to add tracks to.
   * @param tracks A list of the paths for the tracks to be added in the playlist.
   * @return An {@link Observable} that will emit the success status of the request and complete.
   */
  @Override public Observable<Boolean> addToPlaylist(long id, List<String> tracks) {
    final PlaylistRequest request = new PlaylistRequest();
    request.setList(tracks);
    return service.addTracksToPlaylist((int) id, request)
        .map(baseResponse -> baseResponse.getCode() == Code.SUCCESS);
  }
}
