package com.kelsos.mbrc.features.library

import androidx.annotation.IdRes
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.queue.Queue

class PopupActionHandler {

  @Queue.Action
  fun albumSelected(
    @IdRes itemId: Int
  ): String {
    if (itemId == R.id.popup_album_tracks) {
      return Queue.DEFAULT
    }

    return when (itemId) {
      R.id.popup_album_queue_next -> Queue.NEXT
      R.id.popup_album_queue_last -> Queue.LAST
      R.id.popup_album_play -> Queue.NOW
      else -> Queue.NOW
    }
  }

  @Queue.Action
  fun artistSelected(
    @IdRes itemId: Int
  ): String {
    if (itemId == R.id.popup_artist_album) {
      return Queue.DEFAULT
    }

    return when (itemId) {
      R.id.popup_artist_queue_next -> Queue.NEXT
      R.id.popup_artist_queue_last -> Queue.LAST
      R.id.popup_artist_play -> Queue.NOW
      else -> Queue.NOW
    }
  }

  @Queue.Action
  fun genreSelected(
    @IdRes itemId: Int
  ): String {
    if (R.id.popup_genre_artists == itemId) {
      return Queue.DEFAULT
    }

    return when (itemId) {
      R.id.popup_genre_queue_next -> Queue.NEXT
      R.id.popup_genre_queue_last -> Queue.LAST
      R.id.popup_genre_play -> Queue.NOW
      else -> Queue.NOW
    }
  }

  @Queue.Action
  fun trackSelected(@IdRes itemId: Int): String =
    when (itemId) {
      R.id.popup_track_queue_next -> Queue.NEXT
      R.id.popup_track_queue_last -> Queue.LAST
      R.id.popup_track_play -> Queue.NOW
      R.id.popup_track_play_queue_all -> Queue.ADD_ALL
      R.id.popup_track_play_artist -> Queue.PLAY_ARTIST
      R.id.popup_track_play_album -> Queue.PLAY_ALBUM
      else -> Queue.NOW
    }
}
