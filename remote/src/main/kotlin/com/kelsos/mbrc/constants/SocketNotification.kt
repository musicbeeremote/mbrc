package com.kelsos.mbrc.constants

import android.support.annotation.StringDef

object SocketNotification {
  const val MUTE = "mute-status-changed"
  const val VOLUME = "volume-changed"
  const val NOW_PLAYING = "nowplaying-list-changed"
  const val PLAY_STATUS = "play-status-changed"
  const val COVER = "cover-changed"
  const val LYRICS = "lyrics-changed"
  const val TRACK = "track-changed"
  const val REPEAT = "repeat-status-changed"

  @Retention(AnnotationRetention.SOURCE)
  @StringDef(MUTE, VOLUME, NOW_PLAYING, PLAY_STATUS, COVER, LYRICS, TRACK, REPEAT)
  annotation class Context
}
