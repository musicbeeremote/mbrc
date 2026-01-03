package com.kelsos.mbrc.core.queue

import com.kelsos.mbrc.core.common.settings.TrackAction

sealed class Queue(val action: String) {
  data object Next : Queue(NEXT)

  data object Last : Queue(LAST)

  data object Now : Queue(NOW)

  data object AddAll : Queue(ADD_ALL)

  data object PlayAlbum : Queue(PLAY_ALBUM)

  data object PlayArtist : Queue(PLAY_ARTIST)

  data object Default : Queue(DEFAULT)

  companion object {
    const val NEXT = "next"
    const val LAST = "last"
    const val NOW = "now"
    const val ADD_ALL = "add-all"
    const val PLAY_ALBUM = "play-album"
    const val PLAY_ARTIST = "play-artist"
    const val DEFAULT = "default"

    fun fromString(string: String): Queue = when (string) {
      NEXT -> Next
      LAST -> Last
      NOW -> Now
      ADD_ALL -> AddAll
      PLAY_ALBUM -> PlayAlbum
      PLAY_ARTIST -> PlayArtist
      DEFAULT -> Default
      else -> throw IllegalArgumentException("$string is not a recognized option")
    }

    fun fromTrackAction(trackAction: TrackAction): Queue = when (trackAction) {
      TrackAction.QueueNext -> Next
      TrackAction.QueueLast -> Last
      TrackAction.PlayNow -> Now
      TrackAction.PlayNowQueueAll -> AddAll
    }
  }
}
