package com.kelsos.mbrc.helper

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.kelsos.mbrc.R
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.data.Queue
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.ui.activities.profile.AlbumTracksActivity
import com.kelsos.mbrc.ui.activities.profile.ArtistAlbumsActivity
import com.kelsos.mbrc.ui.activities.profile.GenreArtistsActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopupActionHandler
@Inject
constructor(private val bus: RxBus, private val settings: BasicSettingsHelper) {

  fun albumSelected(menuItem: MenuItem, entry: Album, context: Context) {

    val query = entry.album!!

    var ua: UserAction? = null
    when (menuItem.itemId) {
      R.id.popup_album_queue_next -> ua = UserAction(Protocol.LibraryQueueAlbum, Queue(Queue.NEXT, query))
      R.id.popup_album_queue_last -> ua = UserAction(Protocol.LibraryQueueAlbum, Queue(Queue.LAST, query))
      R.id.popup_album_play -> ua = UserAction(Protocol.LibraryQueueAlbum, Queue(Queue.NOW, query))
      R.id.popup_album_tracks -> openProfile(entry, context)
      else -> {
      }
    }

    if (ua != null) {
      bus.post(MessageEvent(ProtocolEventType.UserAction, ua))
    }
  }

  fun artistSelected(menuItem: MenuItem, entry: Artist, context: Context) {
    val query = entry.artist!!

    var ua: UserAction? = null
    when (menuItem.itemId) {
      R.id.popup_artist_queue_next -> ua = UserAction(Protocol.LibraryQueueArtist, Queue(Queue.NEXT, query))
      R.id.popup_artist_queue_last -> ua = UserAction(Protocol.LibraryQueueArtist, Queue(Queue.LAST, query))
      R.id.popup_artist_play -> ua = UserAction(Protocol.LibraryQueueArtist, Queue(Queue.NOW, query))
      R.id.popup_artist_album -> openProfile(entry, context)
      else -> {
      }
    }

    if (ua != null) {
      bus.post(MessageEvent(ProtocolEventType.UserAction, ua))
    }
  }

  fun genreSelected(menuItem: MenuItem, entry: Genre, context: Context) {
    val query = entry.genre!!

    var ua: UserAction? = null
    when (menuItem.itemId) {
      R.id.popup_genre_queue_next -> ua = UserAction(Protocol.LibraryQueueGenre, Queue(Queue.NEXT, query))
      R.id.popup_genre_queue_last -> ua = UserAction(Protocol.LibraryQueueGenre, Queue(Queue.LAST, query))
      R.id.popup_genre_play -> ua = UserAction(Protocol.LibraryQueueGenre, Queue(Queue.NOW, query))
      R.id.popup_genre_artists -> openProfile(entry, context)
      else -> {
      }
    }

    if (ua != null) {
      bus.post(MessageEvent(ProtocolEventType.UserAction, ua))
    }
  }

  fun trackSelected(menuItem: MenuItem, entry: Track) {
    val query = entry.src!!

    var ua: UserAction? = null
    when (menuItem.itemId) {
      R.id.popup_track_queue_next -> ua = UserAction(Protocol.LibraryQueueTrack, Queue(Queue.NEXT, query))
      R.id.popup_track_queue_last -> ua = UserAction(Protocol.LibraryQueueTrack, Queue(Queue.LAST, query))
      R.id.popup_track_play -> ua = UserAction(Protocol.LibraryQueueTrack, Queue(Queue.NOW, query))
      else -> {
      }
    }

    if (ua != null) {
      bus.post(MessageEvent(ProtocolEventType.UserAction, ua))
    }
  }

  fun albumSelected(album: Album, context: Context) {
    val defaultAction = settings.defaultAction
    if (defaultAction != Const.SUB) {
      //noinspection WrongConstant
      val queue = Queue(defaultAction, album.album!!)
      val data = UserAction(Protocol.LibraryQueueAlbum, queue)
      val event = MessageEvent(ProtocolEventType.UserAction, data)
      bus.post(event)
    } else {
      openProfile(album, context)
    }
  }

  fun artistSelected(artist: Artist, context: Context) {
    val defaultAction = settings.defaultAction
    if (defaultAction != Const.SUB) {
      //noinspection WrongConstant
      val queue = Queue(defaultAction, artist.artist!!)
      val data = UserAction(Protocol.LibraryQueueArtist, queue)
      val event = MessageEvent(ProtocolEventType.UserAction, data)
      bus.post(event)
    } else {
      openProfile(artist, context)
    }
  }

  fun genreSelected(genre: Genre, context: Context) {
    val defaultAction = settings.defaultAction
    if (defaultAction != Const.SUB) {
      //noinspection WrongConstant
      val queue = Queue(defaultAction, genre.genre!!)
      val action = UserAction(Protocol.LibraryQueueGenre, queue)
      val event = MessageEvent(ProtocolEventType.UserAction, action)
      bus.post(event)
    } else {
      openProfile(genre, context)
    }
  }

  fun trackSelected(track: Track) {
    var defaultAction = settings.defaultAction
    if (Const.SUB == defaultAction) {
      defaultAction = Queue.NOW
    }
    //noinspection WrongConstant
    val queue = Queue(defaultAction, track.src!!)
    val action = UserAction(Protocol.LibraryQueueTrack, queue)
    val event = MessageEvent(ProtocolEventType.UserAction, action)
    bus.post(event)
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
