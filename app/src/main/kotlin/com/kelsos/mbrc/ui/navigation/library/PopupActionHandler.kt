package com.kelsos.mbrc.ui.navigation.library

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumMapper
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.content.nowplaying.queue.Queue.QueueType
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksActivity
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsActivity
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsActivity
import com.kelsos.mbrc.utilities.SchedulerProvider
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
    private val schedulerProvider: SchedulerProvider,
    private val trackRepository: TrackRepository,
    private val queueApi: QueueApi
) {

  private val disposables = CompositeDisposable()

  fun albumSelected(
      menuItem: MenuItem,
      entry: AlbumEntity,
      context: Context,
      result: (success: Boolean) -> Unit = {}
  ) {

    if (menuItem.itemId == R.id.popup_album_tracks) {
      openProfile(entry, context)
      return
    }

    val type = when (menuItem.itemId) {
      R.id.popup_album_queue_next -> Queue.NEXT
      R.id.popup_album_queue_last -> Queue.LAST
      R.id.popup_album_play -> Queue.NOW
      else -> Queue.NOW
    }

    queueAlbum(entry, type, result)
  }

  private fun queueAlbum(
      entry: AlbumEntity,
      @QueueType type: String,
      result: (success: Boolean) -> Unit
  ) {
    disposables += trackRepository.getAlbumTrackPaths(entry.album, entry.artist)
        .flatMap {
          queueApi.queue(type, it)
        }.subscribeOn(schedulerProvider.io())
        .subscribe({
          result(true)
        }) {
          result(false)
          Timber.e(it, "Failed to queue")
        }
  }

  fun artistSelected(
      menuItem: MenuItem,
      entry: ArtistEntity,
      context: Context,
      result: (success: Boolean) -> Unit = {}
  ) {

    if (menuItem.itemId == R.id.popup_artist_album) {
      openProfile(entry, context)
      return
    }

    val type = when (menuItem.itemId) {
      R.id.popup_artist_queue_next -> Queue.NEXT
      R.id.popup_artist_queue_last -> Queue.LAST
      R.id.popup_artist_play -> Queue.NOW
      else -> Queue.NOW
    }

    queueArtist(entry, type, result)
  }

  private fun queueArtist(entry: ArtistEntity, type: String, result: (success: Boolean) -> Unit) {
    disposables += trackRepository.getArtistTrackPaths(artist = entry.artist).flatMap {
      queueApi.queue(type, it)
    }.subscribeOn(schedulerProvider.io()).subscribe({
      result(true)
    }) {
      result(false)
      Timber.e(it, "Failed to queue")
    }
  }

  fun genreSelected(
      menuItem: MenuItem,
      entry: GenreEntity,
      context: Context,
      result: (success: Boolean) -> Unit = {}
  ) {

    if (R.id.popup_genre_artists == menuItem.itemId) {
      openProfile(entry, context)
      return
    }

    val type = when (menuItem.itemId) {
      R.id.popup_genre_queue_next -> Queue.NEXT
      R.id.popup_genre_queue_last -> Queue.LAST
      R.id.popup_genre_play -> Queue.NOW
      else -> Queue.NOW
    }

    queueGenre(entry, type, result)
  }

  private fun queueGenre(entry: GenreEntity, type: String, result: (success: Boolean) -> Unit) {
    disposables += trackRepository.getGenreTrackPaths(genre = entry.genre)
        .flatMap {
          queueApi.queue(type, it)
        }.subscribeOn(schedulerProvider.io())
        .subscribe({
          result(true)
        }) {
          result(false)
          Timber.e(it, "Failed to queue")
        }
  }

  //todo album detection -> queue album tracks
  fun trackSelected(menuItem: MenuItem, entry: TrackEntity, album: Boolean = false) {
    val type = when (menuItem.itemId) {
      R.id.popup_track_queue_next -> Queue.NEXT
      R.id.popup_track_queue_last -> Queue.LAST
      R.id.popup_track_play -> Queue.NOW
      R.id.popup_track_play_queue_all -> Queue.ADD_ALL
      else -> Queue.NOW
    }

    queueTrack(entry, type, album)
  }

  private fun queueTrack(entry: TrackEntity, @QueueType type: String, album: Boolean = false) {

    val trackSource: Single<List<String>>
    val path: String?
    if (type == Queue.ADD_ALL) {
      if (album) {
        trackSource = trackRepository.getAlbumTrackPaths(entry.album, entry.albumArtist)
      } else {
        trackSource = trackRepository.getAllTrackPaths()
      }

      path = entry.src

    } else {
      trackSource = Single.fromCallable {
        val list = listOf(entry.src)
        return@fromCallable list
      }
      path = null
    }

    disposables += trackSource.flatMap { queueApi.queue(type, it, path) }
        .subscribeOn(schedulerProvider.io())
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
