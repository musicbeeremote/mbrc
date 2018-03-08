package com.kelsos.mbrc.ui.navigation.library

import android.content.Context
import android.content.Intent
import androidx.annotation.IdRes
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumMapper
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.ui.navigation.library.albumtracks.AlbumTracksActivity
import com.kelsos.mbrc.ui.navigation.library.artistalbums.ArtistAlbumsActivity
import com.kelsos.mbrc.ui.navigation.library.genreartists.GenreArtistsActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PopupActionHandler
@Inject
constructor() {

  @LibraryPopup.Action
  fun albumSelected(
    @IdRes itemId: Int,
    entry: Album,
    context: Context
  ): String {
    if (itemId == R.id.popup_album_tracks) {
      openProfile(entry, context)
      return LibraryPopup.PROFILE
    }

    return when (itemId) {
      R.id.popup_album_queue_next -> LibraryPopup.NEXT
      R.id.popup_album_queue_last -> LibraryPopup.LAST
      R.id.popup_album_play -> LibraryPopup.NOW
      else -> LibraryPopup.NOW
    }
  }

  @LibraryPopup.Action
  fun artistSelected(
    @IdRes itemId: Int,
    entry: Artist,
    context: Context
  ): String {
    if (itemId == R.id.popup_artist_album) {
      openProfile(entry, context)
      return LibraryPopup.PROFILE
    }

    return when (itemId) {
      R.id.popup_artist_queue_next -> LibraryPopup.NEXT
      R.id.popup_artist_queue_last -> LibraryPopup.LAST
      R.id.popup_artist_play -> LibraryPopup.NOW
      else -> LibraryPopup.NOW
    }
  }

  @LibraryPopup.Action
  fun genreSelected(
    @IdRes itemId: Int,
    entry: Genre,
    context: Context
  ): String {
    if (R.id.popup_genre_artists == itemId) {
      openProfile(entry, context)
      return LibraryPopup.PROFILE
    }

    return when (itemId) {
      R.id.popup_genre_queue_next -> LibraryPopup.NEXT
      R.id.popup_genre_queue_last -> LibraryPopup.LAST
      R.id.popup_genre_play -> LibraryPopup.NOW
      else -> LibraryPopup.NOW
    }
  }

  @LibraryPopup.Action
  fun trackSelected(@IdRes itemId: Int): String =
    when (itemId) {
      R.id.popup_track_queue_next -> LibraryPopup.NEXT
      R.id.popup_track_queue_last -> LibraryPopup.LAST
      R.id.popup_track_play -> LibraryPopup.NOW
      R.id.popup_track_play_queue_all -> LibraryPopup.ADD_ALL
      R.id.popup_track_play_artist -> LibraryPopup.PLAY_ARTIST
      R.id.popup_track_play_album -> LibraryPopup.PLAY_ALBUM
      else -> LibraryPopup.NOW
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
