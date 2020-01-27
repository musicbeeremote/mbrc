package com.kelsos.mbrc.features.library

import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.queue.Queue.Action
import com.kelsos.mbrc.features.queue.Queue.DEFAULT
import com.kelsos.mbrc.features.queue.Queue.PROFILE
import com.kelsos.mbrc.features.queue.QueueApi
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore

class PopupActionHandler(
  private val settings: DefaultActionPreferenceStore,
  private val trackRepository: TrackRepository,
  private val queueApi: QueueApi
) {

  fun queue(
    @Action action: String,
    entry: Album
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueApi.queue(action, trackRepository.getAlbumTrackPaths(entry.album, entry.artist))
  }

  fun queue(
    @Action action: String,
    entry: Artist
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueApi.queue(action, trackRepository.getArtistTrackPaths(artist = entry.artist))
  }

  fun queue(
    @Action action: String,
    entry: Genre
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueApi.queue(action, trackRepository.getGenreTrackPaths(genre = entry.genre))
  }

  fun queue(
    entry: Track,
    album: Boolean = false,
    @Action action: String = DEFAULT
  ) {
    val actualAction = if (action == DEFAULT) {
      settings.defaultAction
    } else {
      action
    }
    val paths: List<String>
    val path: String?
    if (actualAction == Queue.ADD_ALL) {
      paths = if (album) {
        trackRepository.getAlbumTrackPaths(entry.album, entry.albumArtist)
      } else {
        trackRepository.getAllTrackPaths()
      }

      path = entry.src
    } else {
      paths = listOf(entry.src)
      path = null
    }
    queueApi.queue(actualAction, paths, path)
  }
}