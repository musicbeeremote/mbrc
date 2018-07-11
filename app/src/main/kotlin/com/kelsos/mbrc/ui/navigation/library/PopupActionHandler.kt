package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import com.kelsos.mbrc.utilities.AppRxSchedulers
import kotlinx.coroutines.experimental.launch

class PopupActionHandler(
  private val settings: DefaultActionPreferenceStore,
  private val appRxSchedulers: AppRxSchedulers,
  private val trackRepository: TrackRepository,
  private val queueApi: QueueApi
) {

  fun albumSelected(
    @Action action: String,
    entry: AlbumEntity,
    result: (success: Boolean) -> Unit = {}
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueAlbum(entry, action, result)
  }

  private fun queueAlbum(
    entry: AlbumEntity,
    @Action type: String,
    result: (success: Boolean) -> Unit
  ) {
    launch {
      val paths = trackRepository.getAlbumTrackPaths(entry.album, entry.artist)
      val response = queueApi.queue(type, paths)
    }
  }

  fun artistSelected(
    @Action action: String,
    entry: ArtistEntity,
    result: (success: Boolean) -> Unit = {}
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueArtist(entry, action, result)
  }

  private fun queueArtist(entry: ArtistEntity, type: String, result: (success: Boolean) -> Unit) {
    val paths = trackRepository.getArtistTrackPaths(artist = entry.artist)
    queueApi.queue(type, paths)
  }

  fun genreSelected(
    @Action action: String,
    entry: GenreEntity,
    result: (success: Boolean) -> Unit = {}
  ) {
    require(action != PROFILE) { "action should not be profile" }
    queueGenre(entry, action, result)
  }

  private fun queueGenre(entry: GenreEntity, type: String, result: (success: Boolean) -> Unit) {
    val paths = trackRepository.getGenreTrackPaths(genre = entry.genre)
    queueApi.queue(type, paths)
  }

  //todo album detection -> queue album tracks
  fun trackSelected(
    @Action action: String,
    entry: TrackEntity,
    album: Boolean = false
  ) {
    queueTrack(entry, action, album)
  }

  private fun queueTrack(entry: TrackEntity, @Action type: String, album: Boolean = false) {

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

  fun trackSelected(track: TrackEntity, album: Boolean = false) {
    queueTrack(track, settings.defaultAction, album)
  }
}