package com.kelsos.mbrc.content.nowplaying.queue

import androidx.annotation.StringDef

object LibraryPopup {
  @StringDef(
    PROFILE,
    NEXT,
    LAST,
    NOW,
    ADD_ALL,
    PLAY_ALBUM,
    PLAY_ARTIST
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Action

  const val PROFILE = "profile"
  const val NEXT = "next"
  const val LAST = "last"
  const val NOW = "now"
  const val ADD_ALL = "add-all"
  const val PLAY_ALBUM = "play-album"
  const val PLAY_ARTIST = "play-artist"
}
