package com.kelsos.mbrc.ui.navigation.library

import androidx.annotation.IdRes
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup

class PopupActionHandler {

  @LibraryPopup.Action
  fun albumSelected(
    @IdRes itemId: Int
  ): String {
    if (itemId == R.id.popup_album_tracks) {
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
    @IdRes itemId: Int
  ): String {
    if (itemId == R.id.popup_artist_album) {
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
    @IdRes itemId: Int
  ): String {
    if (R.id.popup_genre_artists == itemId) {
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
}
