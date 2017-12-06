package com.kelsos.mbrc.ui.navigation.library

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumMapper
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.content.nowplaying.queue.Queue.QueueType
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.preferences.DefaultActionPreferenceStore
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksActivity
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsActivity
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsActivity
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PopupActionHandler
@Inject
constructor(private val settings: DefaultActionPreferenceStore,
            @Named("io") private val ioScheduler: Scheduler,
            private val trackRepository: TrackRepository,
            private val queueApi: QueueApi) {

  fun albumSelected(menuItem: MenuItem, entry: Album, context: Context) {

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

    queueAlbum(entry, type)
  }

  private fun queueAlbum(entry: Album, @QueueType type: String) {
    trackRepository.getAlbumTrackPaths(entry.album!!, entry.artist!!).flatMap {
      queueApi.queue(type, it)
    }.subscribeOn(ioScheduler).subscribe({

    }) {
      Timber.v(it, "Failed to queue")
    }
  }

  fun artistSelected(menuItem: MenuItem, entry: Artist, context: Context) {

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

    queueArtist(entry, type)
  }

  private fun queueArtist(entry: Artist, type: String) {
    trackRepository.getArtistTrackPaths(artist = entry.artist!!).flatMap {
      queueApi.queue(type, it)
    }.subscribeOn(ioScheduler).subscribe({

    }) {
      Timber.v(it, "Failed to queue")
    }
  }

  fun genreSelected(menuItem: MenuItem, entry: Genre, context: Context) {

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

    queueGenre(entry, type)
  }

  private fun queueGenre(entry: Genre, type: String) {
    trackRepository.getGenreTrackPaths(genre = entry.genre!!).flatMap {
      queueApi.queue(type, it)
    }.subscribeOn(ioScheduler).subscribe({

    }) {
      Timber.v(it, "Failed to queue")
    }
  }

  //todo album detection -> queue album tracks
  fun trackSelected(menuItem: MenuItem, entry: Track, album: Boolean = false) {
    val type = when (menuItem.itemId) {
      R.id.popup_track_queue_next -> Queue.NEXT
      R.id.popup_track_queue_last -> Queue.LAST
      R.id.popup_track_play -> Queue.NOW
      R.id.popup_track_play_queue_all -> Queue.ADD_ALL
      else -> Queue.NOW
    }

    queueTrack(entry, type, album)
  }

  private fun queueTrack(entry: Track, @QueueType type: String, album: Boolean = false) {

    val trackSource: Single<List<String>>
    val path:String?
    if (type == Queue.ADD_ALL) {
      if (album) {
        trackSource = trackRepository.getAlbumTrackPaths(entry.album!!, entry.albumArtist!!)
      } else {
        trackSource = trackRepository.getAllTrackPaths()
      }

      path = entry.src

    } else {
      trackSource = Single.fromCallable {
        val list = listOf(entry.src!!)
        return@fromCallable list
      }
      path = null
    }

    trackSource.flatMap { queueApi.queue(type, it, path) }
        .subscribeOn(ioScheduler)
        .subscribe({ }) {
          Timber.v(it, "Failed to queue")
        }
  }

  fun albumSelected(album: Album, context: Context) {
    openProfile(album, context)
  }

  fun artistSelected(artist: Artist, context: Context) {
    openProfile(artist, context)
  }

  fun genreSelected(genre: Genre, context: Context) {
    openProfile(genre, context)
  }

  fun trackSelected(track: Track, album: Boolean = false) {
    queueTrack(track, settings.defaultAction, album)
  }

  private fun openProfile(artist: Artist, context: Context) {
    val intent = Intent(context, ArtistAlbumsActivity::class.java)
    intent.putExtra(ArtistAlbumsActivity.ARTIST_NAME, artist.artist)
    context.startActivity(intent)
  }

  private fun openProfile(album: Album, context: Context) {
    val mapper = AlbumMapper()
    val intent = Intent(context, AlbumTracksActivity::class.java)
    intent.putExtra(AlbumTracksActivity.ALBUM, mapper.map(album))
    context.startActivity(intent)
  }

  private fun openProfile(genre: Genre, context: Context) {
    val intent = Intent(context, GenreArtistsActivity::class.java)
    intent.putExtra(GenreArtistsActivity.GENRE_NAME, genre.genre)
    context.startActivity(intent)
  }
}
