package com.kelsos.mbrc.features.library

import com.kelsos.mbrc.features.library.albums.Album
import com.kelsos.mbrc.features.library.artists.Artist
import com.kelsos.mbrc.features.library.genres.Genre
import com.kelsos.mbrc.features.library.tracks.Track
import com.kelsos.mbrc.features.library.tracks.TrackRepository
import com.kelsos.mbrc.features.queue.LibraryPopup
import com.kelsos.mbrc.features.queue.LibraryPopup.Action
import com.kelsos.mbrc.features.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.features.queue.QueueApi
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore

class PopupActionHandler(
  private val settings: DefaultActionPreferenceStore,
  private val trackRepository: TrackRepository,
  private val queueApi: QueueApi
) {

  fun albumSelected(
    @Action action: String,
    entry: Album,
    result: (success: Boolean) -> Unit = {}
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueAlbum(entry, action, result)
  }

  private fun queueAlbum(
    entry: Album,
    @Action type: String,
    result: (success: Boolean) -> Unit
  ) {
    val paths = trackRepository.getAlbumTrackPaths(entry.album, entry.artist)
    val response = queueApi.queue(type, paths)
  }

  fun artistSelected(
    @Action action: String,
    entry: Artist,
    result: (success: Boolean) -> Unit = {}
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueArtist(entry, action, result)
  }

  private fun queueArtist(entry: Artist, type: String, result: (success: Boolean) -> Unit) {
    val paths = trackRepository.getArtistTrackPaths(artist = entry.artist)
    queueApi.queue(type, paths)
  }

  fun genreSelected(
    @Action action: String,
    entry: Genre,
    result: (success: Boolean) -> Unit = {}
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueGenre(entry, action, result)
  }

  private fun queueGenre(entry: Genre, type: String, result: (success: Boolean) -> Unit) {
    val paths = trackRepository.getGenreTrackPaths(genre = entry.genre)
    queueApi.queue(type, paths)
  }

  // todo album detection -> queue album tracks
  fun trackSelected(
    @Action action: String,
    entry: Track,
    album: Boolean = false
  ) {
    queueTrack(entry, action, album)
  }

  private fun queueTrack(entry: Track, @Action type: String, album: Boolean = false) {

    val paths: List<String>
    val path: String?
    if (type == LibraryPopup.ADD_ALL) {
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

    queueApi.queue(type, paths, path)
  }

  fun trackSelected(track: Track, album: Boolean = false) {
    queueTrack(track, settings.defaultAction, album)
  }
}