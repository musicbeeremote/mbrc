package com.kelsos.mbrc.preferences

import androidx.annotation.StringDef
import org.threeten.bp.Instant

interface SettingsManager {
  @CallAction fun getCallAction(): String

  @StringDef(
    NONE,
    PAUSE,
    STOP,
    REDUCE
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class CallAction

  companion object {
    const val NONE = "none"
    const val PAUSE = "pause"
    const val STOP = "stop"
    const val REDUCE = "reduce"
  }
  suspend fun shouldDisplayOnlyAlbumArtists(): Boolean
  fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
  fun shouldShowChangeLog(): Boolean
  fun isPluginUpdateCheckEnabled(): Boolean
  fun getLastUpdated(required: Boolean = false): Instant
  fun setLastUpdated(lastChecked: Instant, required: Boolean = false)
  fun getClientId(): String
}
