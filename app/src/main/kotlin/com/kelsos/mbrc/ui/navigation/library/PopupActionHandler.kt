package com.kelsos.mbrc.ui.navigation.library

import android.content.Context
import android.content.Intent
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumMapper
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.Action
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.PROFILE
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksActivity
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsActivity
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsActivity
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopupActionHandler
@Inject
constructor(
  private val settings: DefaultActionPreferenceStore,
  private val appRxSchedulers: AppRxSchedulers,
  private val trackRepository: TrackRepository,
  private val queueApi: QueueApi
) {

  private val disposables = CompositeDisposable()

  fun albumSelected(
    @Action action: String,
    entry: AlbumEntity,
    context: Context,
    result: (success: Boolean) -> Unit = {}
  ) {

    if (action == LibraryPopup.PROFILE) {
      openProfile(entry, context)
      return
    }

    queueAlbum(entry, action, result)
  }

  private fun queueAlbum(
    entry: AlbumEntity,
    @Action type: String,
    result: (success: Boolean) -> Unit
  ) {
    disposables += trackRepository.getAlbumTrackPaths(entry.album, entry.artist)
      .flatMap {
        queueApi.queue(type, it)
      }.subscribeOn(appRxSchedulers.disk)
      .subscribe({
        result(true)
      }) {
        result(false)
        Timber.e(it, "Failed to queue")
      }
  }

  fun artistSelected(
    @Action action: String,
    entry: ArtistEntity,
    context: Context,
    result: (success: Boolean) -> Unit = {}
  ) {

    if (action == PROFILE) {
      openProfile(entry, context)
      return
    }

    queueArtist(entry, action, result)
  }

  private fun queueArtist(entry: ArtistEntity, type: String, result: (success: Boolean) -> Unit) {
    disposables += trackRepository.getArtistTrackPaths(artist = entry.artist).flatMap {
      queueApi.queue(type, it)
    }.subscribeOn(appRxSchedulers.disk).subscribe({
      result(true)
    }) {
      result(false)
      Timber.e(it, "Failed to queue")
    }
  }

  fun genreSelected(
    @Action action: String,
    entry: GenreEntity,
    context: Context,
    result: (success: Boolean) -> Unit = {}
  ) {

    if (action == PROFILE) {
      openProfile(entry, context)
      return
    }

    queueGenre(entry, action, result)
  }

  private fun queueGenre(entry: GenreEntity, type: String, result: (success: Boolean) -> Unit) {
    disposables += trackRepository.getGenreTrackPaths(genre = entry.genre)
      .flatMap {
        queueApi.queue(type, it)
      }.subscribeOn(appRxSchedulers.disk)
      .subscribe({
        result(true)
      }) {
        result(false)
        Timber.e(it, "Failed to queue")
      }
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

    val trackSource: Single<List<String>>
    val path: String?
    if (type == LibraryPopup.ADD_ALL) {
      trackSource = if (album) {
        trackRepository.getAlbumTrackPaths(entry.album, entry.albumArtist)
      } else {
        trackRepository.getAllTrackPaths()
      }

      path = entry.src
    } else {
      trackSource = Single.fromCallable {
        return@fromCallable listOf(entry.src)
      }
      path = null
    }

    disposables += trackSource.flatMap { queueApi.queue(type, it, path) }
      .subscribeOn(appRxSchedulers.disk)
      .subscribe({ }) {
        Timber.v(it, "Failed to queue")
      }
  }

  fun albumSelected(album: AlbumEntity, context: Context) {
    openProfile(album, context)
  }

  fun artistSelected(artist: ArtistEntity, context: Context) {
    openProfile(artist, context)
  }

  fun genreSelected(genre: GenreEntity, context: Context) {
    openProfile(genre, context)
  }

  fun trackSelected(track: TrackEntity, album: Boolean = false) {
    queueTrack(track, settings.defaultAction, album)
  }

  private fun openProfile(artist: ArtistEntity, context: Context) {
    val intent = Intent(context, ArtistAlbumsActivity::class.java)
    intent.putExtra(ArtistAlbumsActivity.ARTIST_NAME, artist.artist)
    context.startActivity(intent)
  }

  private fun openProfile(album: AlbumEntity, context: Context) {
    val mapper = AlbumMapper()
    val intent = Intent(context, AlbumTracksActivity::class.java)
    intent.putExtra(AlbumTracksActivity.ALBUM, mapper.map(album))
    context.startActivity(intent)
  }

  private fun openProfile(genre: GenreEntity, context: Context) {
    val intent = Intent(context, GenreArtistsActivity::class.java)
    intent.putExtra(GenreArtistsActivity.GENRE_NAME, genre.genre)
    context.startActivity(intent)
  }
}