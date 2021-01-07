package com.kelsos.mbrc.features.library

import androidx.annotation.IdRes
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.queue.Queue

class PopupActionHandler {

  fun albumSelected(
    @IdRes itemId: Int
  ): Queue {
    if (itemId == R.id.popup_album_tracks) {
      return Queue.Default
    }

    return when (itemId) {
      R.id.popup_album_queue_next -> Queue.Next
      R.id.popup_album_queue_last -> Queue.Last
      R.id.popup_album_play -> Queue.Now
      else -> Queue.Now
    }
  }

  fun artistSelected(
    @IdRes itemId: Int
  ): Queue {
    if (itemId == R.id.popup_artist_album) {
      return Queue.Default
    }

    return when (itemId) {
      R.id.popup_artist_queue_next -> Queue.Next
      R.id.popup_artist_queue_last -> Queue.Last
      R.id.popup_artist_play -> Queue.Now
      else -> Queue.Now
    }
  }

  fun genreSelected(
    @IdRes itemId: Int
  ): Queue {
    if (R.id.popup_genre_artists == itemId) {
      return Queue.Default
    }

    return when (itemId) {
      R.id.popup_genre_queue_next -> Queue.Next
      R.id.popup_genre_queue_last -> Queue.Last
      R.id.popup_genre_play -> Queue.Now
      else -> Queue.Now
    }
  }

  fun trackSelected(@IdRes itemId: Int): Queue =
    when (itemId) {
      R.id.popup_track_queue_next -> Queue.Next
      R.id.popup_track_queue_last -> Queue.Last
      R.id.popup_track_play -> Queue.Now
      R.id.popup_track_play_queue_all -> Queue.AddAll
      R.id.popup_track_play_artist -> Queue.PlayArtist
      R.id.popup_track_play_album -> Queue.PlayAlbum
      else -> Queue.Now
    }
}
