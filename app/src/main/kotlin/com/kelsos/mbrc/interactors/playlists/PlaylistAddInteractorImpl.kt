package com.kelsos.mbrc.interactors.playlists

import com.kelsos.mbrc.constants.Code
import com.kelsos.mbrc.dto.requests.PlaylistRequest
import com.kelsos.mbrc.services.api.PlaylistService
import rx.Observable
import javax.inject.Inject

class PlaylistAddInteractorImpl
@Inject constructor(private val service: PlaylistService) : PlaylistAddInteractor {

  /**
   * Creates a new playlist.

   * @param name The name of the new playlist.
   * *
   * @param tracks A list of the paths for the tracks to be in the playlist.
   * *
   * @return An [Observable] that will emit the success status of the request and complete.
   */
  override fun createPlaylist(name: String, tracks: List<String>): Observable<Boolean> {
    val request = PlaylistRequest()
    request.name = name
    request.list = tracks
    return service.createPlaylist(request).map { it.code == Code.SUCCESS }
  }

  /**
   * Adds tracks to an existing playlist.

   * @param id The id of the playlist that we want to add tracks to.
   * *
   * @param tracks A list of the paths for the tracks to be added in the playlist.
   * *
   * @return An [Observable] that will emit the success status of the request and complete.
   */
  override fun addToPlaylist(id: Long, tracks: List<String>): Observable<Boolean> {
    val request = PlaylistRequest()
    request.list = tracks
    return service.addTracksToPlaylist(id.toInt(), request)
        .map { it.code == Code.SUCCESS }
  }
}
